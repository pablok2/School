using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Net.Sockets;

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
                Socket socket = listeningSocket.Accept();

                byte[] rec = new byte[1024];
                socket.Receive(rec);

                string incoming = Encoding.UTF8.GetString(rec);
                Console.WriteLine(incoming);

                SendResponse(socket);
                socket.Close();
            }
        }

        private static void SendResponse(Socket socket)
        {
            // Create a response
            DateTime utc = DateTime.UtcNow;

            string html = "<html>" +
                "<head>" +
                "<title>" +
                "Pablo" +
                "</title>" +
                "</head>" +
                "<body bgcolor = \"FF00FF\">" +
                "</body>" +
                 "</html>";

            string response = "HTTP/ 1.1 200 OK\r\n" +
                string.Format("Date: {0}\r\n", utc.ToString("R")) +
                "Server: localhostServer/2.1\r\n" +
                "Content-Type: text/html\r\n" +
                string.Format("Content-Length: {0}\r\n\r\n", html.Length) +
                html;

            byte[] toSend = Encoding.UTF8.GetBytes(response);

            if (socket.Send(toSend, toSend.Length, 0) == -1)
            {
                Console.WriteLine("Transmission failed.");
            }
            else
            {
                Console.WriteLine("Succeeded transmission.");
            }
        }
    }
}
