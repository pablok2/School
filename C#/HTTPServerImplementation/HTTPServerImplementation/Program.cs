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
            Socket listeningSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            listeningSocket.Bind(new IPEndPoint(IPAddress.Parse("127.0.0.1"), 80));
            listeningSocket.Listen(80);

            while (true)
            {
                WebRequest newWebRequest = new WebRequest(listeningSocket.Accept());
                newWebRequest.RootDirectory = @"C:\Users\Pavel\Documents\test2";

                Thread thread = new Thread(() => newWebRequest.StartProcessing());
                thread.Start();
            }
        }
    }
}
