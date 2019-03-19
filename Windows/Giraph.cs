// A Java launcher which is aware of local memory and OS type
using System;
using System.Diagnostics;
using System.Globalization;
using System.Text;
using System.Threading;
using System.Windows.Forms;
using System.IO;

// Compile command is csc /reference:Microsoft.VisualBasic.dll /win32icon:giraph.ico Giraph.cs

namespace SeqMonkLauncher
{
    class SeqMonk
    {
        static void Main(string [] args)
        {

            Console.WriteLine("Finding java");

            string javaPath = getJavaPath();

            if (!(javaPath.Contains("java")))
            {
                MessageBox.Show("Couldn't find java on your system", "Failed to launch Giraph", MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
                Environment.Exit(1);
            }

            Console.WriteLine("Found java at '"+javaPath+"'");


            if (javaPath == "java")
            {
                Console.WriteLine("PATH is '" + System.Environment.GetEnvironmentVariable("PATH") + "'");
            }

            string javaVersion = getJavaVersion(javaPath);

            Console.WriteLine("Java version string is " + javaVersion);

            if (!(javaVersion.Contains("Java") || javaVersion.Contains("OpenJDK")))
            {
                MessageBox.Show("Got an unexpected output from running java -version. Going for it anyway...", "Problem launching Giraph", MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
            }

            int memoryCeiling = 1300;

			Console.WriteLine(javaVersion);
			Console.WriteLine("");

            if (javaVersion.Contains("64-Bit"))
            {
                memoryCeiling = 4096;

                Console.WriteLine("Found 64-bit JVM, setting memory ceiling to 4096m");
            }
            else
            {
                Console.WriteLine("Found 32-bit JVM, setting memory ceiling to 1300m");
                
                // Check if the underlying system is 64 bit.  If it is then tell them they need to install
                // a 64 bit JRE.  We got all kinds of problems trying to run on a 32 bit JRE on a 64 bit system
                // and this is nearly always a mistake, so let's not support it.
                if (Directory.Exists("C:\\Program Files (x86)")) 
                {
	                MessageBox.Show("You appear to be running a 64 bit OS, but only have 32 bit Java.  Please install a 64 bit version of Java", "Wrong version of Java", MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
	                Environment.Exit(1);
                
                }
                
            }


            int physicalMemory = getPhysicalMemory(javaPath);

            Console.WriteLine("Physical memory installed is " + physicalMemory);

            if (physicalMemory < 1024)
            {
                MessageBox.Show("Not enough memory to run Giraph (you need at least 1GB)", "Failed to launch Giraph", MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
                Environment.Exit(1);
            }

            int memoryToUse = (physicalMemory * 2) / 3;

            if (memoryToUse > memoryCeiling)
            {
                memoryToUse = memoryCeiling;
            }

            Console.Write("Amount of memory to use is ");
            Console.Write(memoryToUse);
            Console.WriteLine("");

            int memoryToRequest = correctMemory(memoryToUse, javaPath);

            if (memoryToRequest == 0)
            {
                MessageBox.Show("SeqMonk process failed to start.  Did you move giraph.exe out of the Giraph directory?", "Failed to launch Giraph", MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
                Environment.Exit(1);
            }

            Console.Write("Accounting for VM oddities, amount of memory to request is ");
            Console.Write(memoryToRequest);
            Console.WriteLine("");

            try
            {

                string path = System.IO.Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().GetName().CodeBase);

                if (path.StartsWith("file:\\"))
                {
                    path = path.Substring(6);
                }

                // UNC paths won't have a drive letter so we need to prepend these with
                // a pair of slashes

                if (!path.Substring(1, 1).Equals(":"))
                {
                    path = "\\\\" + path;
                }


				string filename = "";
				
				if (args.Length > 0)
				{
					filename = args[0];
				}

                string finalCommand = "\""+javaPath+"\" -cp \"" + path + ";" + path + "\\commons-math3-3.5.jar\" -Xss4m -Xmx" + memoryToRequest + "m uk.ac.babraham.Giraph.GiraphApplication \""+filename+"\"";

                Console.WriteLine("Final command is " + finalCommand);
                System.Diagnostics.ProcessStartInfo procStartInfo = new System.Diagnostics.ProcessStartInfo(javaPath, "-cp \"" + path + ";" + path + "\\commons-math3-3.5.jar\" -Xss4m -Xmx" + memoryToRequest + "m uk.ac.babraham.Giraph.GiraphApplication \"" + filename + "\"");

                procStartInfo.RedirectStandardOutput = true;
                procStartInfo.UseShellExecute = false;
                procStartInfo.CreateNoWindow = false;
                System.Diagnostics.Process proc = new System.Diagnostics.Process();
                proc.StartInfo = procStartInfo;
                proc.Start();
                string result = proc.StandardOutput.ReadToEnd();
                Console.WriteLine(result);
            }
            catch (Exception objException)
            {
                Console.WriteLine(objException.ToString());
            }

        }

        static int getPhysicalMemory(String javaPath)
        {
            Microsoft.VisualBasic.Devices.ComputerInfo computer = new Microsoft.VisualBasic.Devices.ComputerInfo();
            ulong rawMemory = computer.TotalPhysicalMemory;

            rawMemory /= (1024 * 1024); // Get value in MB

            return (int)rawMemory;

        }

        static int correctMemory(int requestedMemory, String javaPath)
        {
            string path = System.IO.Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().GetName().CodeBase);

            if (path.StartsWith("file:\\"))
            {
                path = path.Substring(6);
            }

            // UNC paths won't have a drive letter so we need to prepend these with
            // a pair of slashes

            if (!path.Substring(1, 1).Equals(":"))
            {
                path = "\\\\" + path;
            }

            // On some 32 bit windows 7 systems people have reported that the jvm creation has
            // failed when using relatively modest amounts of memory (1500m on a machine with 4GB).
            // We're therefore going to try this test a few times to try to get an ammount of memory
            // which will actually work

            int currentRequestAmount = requestedMemory;

            // We'll give up if we need to get below half of the original request amount
            while (currentRequestAmount > requestedMemory / 2)
            {


                string finalCommand = "\""+javaPath+"\" -cp \"" + path + "\" -Xmx" + currentRequestAmount + "m uk.ac.babraham.Giraph.Utilities.ReportMemoryUsage";

                Console.WriteLine("Memcheck command is " + finalCommand);
                string parms = "-cp \"" + path + "\" -Xmx" + currentRequestAmount + "m uk.ac.babraham.SeqMonk.Utilities.ReportMemoryUsage";
                string output = "";
                string error = string.Empty;

                ProcessStartInfo psi = new ProcessStartInfo(javaPath, parms);

                psi.RedirectStandardOutput = true;
                psi.RedirectStandardError = true;
                psi.WindowStyle = System.Diagnostics.ProcessWindowStyle.Normal;
                psi.UseShellExecute = false;
                System.Diagnostics.Process reg;
                reg = System.Diagnostics.Process.Start(psi);
                using (System.IO.StreamReader myOutput = reg.StandardOutput)
                {
                    output = myOutput.ReadToEnd();
                }
                using (System.IO.StreamReader myError = reg.StandardError)
                {
                    error = myError.ReadToEnd();
                }


                // We used to give up if we saw anything on stderr, but sometimes java
                // puts out diagnostic stuff there so this is too harsh.
                if (error.Length > 0)
                {
                    Console.WriteLine("Saw error:" + currentRequestAmount + ":" + error);
                }

                // We can check the exit code to see if it died or not.
                if (reg.ExitCode != 0)
                {
                    Console.WriteLine("Check failed - trying again");
                     currentRequestAmount -= requestedMemory / 10;
                     continue;
                }

                output = output.Replace("\n", "");
                output = output.Replace("\r", "");

                Console.WriteLine("Raw memcheck output was '" + output + "'");

                // If the currentRequestedMemory is lower than the original requested
                // memory then there was a problem with memory settings and we'll just
                // go with what worked.
                if (currentRequestAmount != requestedMemory)
                {
                    return currentRequestAmount;
                }

                // We need to force the double parser to parse numbers with decimal points
                // even if their locale setting says something else.

                CultureInfo culture = new CultureInfo("en-US");

                double actualMemory = double.Parse(output, culture.NumberFormat);

                Console.WriteLine("Parsed memcheck output was " + actualMemory);

                int correctedMemory = (int)(requestedMemory * (requestedMemory / actualMemory));

                Console.WriteLine("Memory corrected by " + requestedMemory + " was " + correctedMemory);

                return correctedMemory;

            }

            Console.WriteLine("Memcheck repeatedly failed.  Giving up.");
            return 0;
        }


        static string getJavaPath ()
        {
            string javaPath = @"java.exe";

            // Check for an embedded jre shipped with seqmonk
            string localPath = AppDomain.CurrentDomain.BaseDirectory + "jre\\bin\\java.exe";
            if (File.Exists(localPath))
            {
                return localPath;
            }
            else
            {
                Console.WriteLine("No local java found at " + localPath);
            }
            return (javaPath);
        }

        static string getJavaVersion(String javaPath)
        {
            try
            {
                string parms = @"-version";
                string output = "";
                string error = string.Empty;

                ProcessStartInfo psi = new ProcessStartInfo(javaPath, parms);

                psi.RedirectStandardOutput = true;
                psi.RedirectStandardError = true;
                psi.WindowStyle = System.Diagnostics.ProcessWindowStyle.Normal;
                psi.UseShellExecute = false;
                System.Diagnostics.Process reg;
                reg = System.Diagnostics.Process.Start(psi);
                using (System.IO.StreamReader myOutput = reg.StandardOutput)
                {
                    output = myOutput.ReadToEnd();
                }
                using (System.IO.StreamReader myError = reg.StandardError)
                {
                    error = myError.ReadToEnd();
                }

                return error;
            }
            catch (Exception objException)
            {
                Console.WriteLine(objException.ToString());
                return "";
            }
        }

    }
}