using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Net.Sockets;
using System.Threading;

namespace HTTPServerImplementation
{
    class Program
    {
        static void Main(string[] args)
        {
            string rootDir = string.Empty;
            int port; 

            if (args.Length == 2)
            {
                rootDir = args[0];
                if (!int.TryParse(args[1], out port))
                {
                    // Default port 80
                    port = 80;
                }

                if (Directory.Exists(rootDir))
                {
                    // Run the server
                    RunServer(rootDir, port);
                }
                else
                {
                    Console.WriteLine("Directory invalid");
                }
            }
            else
            {
                Console.WriteLine("Missing command line arguments: {{ directory }} {{ port number }}");
            }

            // Just to stop at the command line
            Console.ReadKey();
        }

        // Separate method for running server processes
        private static void RunServer(string rootDirectory, int port)
        {
            Socket listeningSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            listeningSocket.Bind(new IPEndPoint(IPAddress.Parse("127.0.0.1"), port)); // Always localhost
            listeningSocket.Listen(80);

            while (true)
            {
                WebRequest newWebRequest = new WebRequest(listeningSocket.Accept())
                {
                    RootDirectory = rootDirectory
                };

                Thread thread = new Thread(() => newWebRequest.StartProcessing());
                thread.Start();
            }
        }
    }
}
