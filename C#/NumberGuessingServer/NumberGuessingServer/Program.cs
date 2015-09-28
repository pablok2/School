using System;
using System.Net;
using System.Text;
using System.Net.Sockets;

namespace NumberGuessingServer
{
    class Program
    {
        static void Main(string[] args)
        {
            // Default telnet port
            int portNumber = 23;

            // Command-line port (if any)
            if (args.Length > 0 && args[0] != string.Empty)
            {
                int.TryParse(args[0], out portNumber);
            }

            // Server socket (Runs on the local machine using localhost)
            Socket listeningSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            listeningSocket.Bind(new IPEndPoint(IPAddress.Parse("127.0.0.1"), portNumber));
            listeningSocket.Listen(portNumber);

            // Server listens for a connection
            while (true)
            {
                try
                {
                    NewGame(listeningSocket.Accept());
                }
                catch (Exception e)
                {
                    Console.WriteLine(e.Message);
                }
            }
        }

        private static void NewGame(Socket conn)
        {
            // Generate a random number
            Random rnd = new Random();
            int randomNumber = rnd.Next(1,101);

            // Hello string
            byte[] hello =
                Encoding.ASCII.GetBytes("+ Hello. I'm thinking of a number between 1 and 100. Can you guess it?");
            byte[] crlf = { 13, 10 }; // CRLF

            // Send the hello string to the client
            conn.Send(hello, 0, hello.Length, 0);
            conn.Send(crlf, 0, crlf.Length, 0);

            Encoding encoder = new ASCIIEncoding();
            byte[] guess = new byte[2];
            int tries = 0;
            bool loser = true;

            // Play until the winning case
            while (loser)
            {
                string inputString = string.Empty;
                bool getMoreInput = true;

                // Get the input from the socket
                while (getMoreInput)
                {
                    conn.Receive(guess, 0, guess.Length, 0);
                    if (guess[0] == crlf[0] && guess[1] == crlf[1])
                    {
                        getMoreInput = false;
                        conn.Send(crlf, 0, crlf.Length, 0);
                    }
                    else
                    {
                        inputString += encoder.GetString(guess, 0, 1);
                    }
                }

                // Check the input for a) validity and b) the number
                int parsed = 0;
                if (!int.TryParse(inputString, out parsed) || parsed < 1 || parsed > 100)
                {
                    byte[] invalid =
                        Encoding.ASCII.GetBytes("! Invalid input, please enter only numbers between 1 and 100.");
                    conn.Send(invalid, 0, invalid.Length, 0);
                    conn.Send(crlf, 0, crlf.Length, 0);
                }
                else
                {
                    // Get comparator
                    int compared = parsed.CompareTo(randomNumber);
                    switch (compared)
                    {
                        // Number too low.
                        case 1:
                        {
                            tries++;
                            byte[] lower = Encoding.ASCII.GetBytes("< My number is lower.");
                            conn.Send(lower, 0, lower.Length, 0);
                            conn.Send(crlf, 0, crlf.Length, 0);
                            break;
                        }

                        // Number too high.
                        case -1:
                        {
                            tries++;
                            byte[] higher = Encoding.ASCII.GetBytes("> Higher.");
                            conn.Send(higher, 0, higher.Length, 0);
                            conn.Send(crlf, 0, crlf.Length, 0);
                            break;
                        }

                        // Winning case
                        case 0:
                        {
                            loser = false;
                            byte[] correct =
                                Encoding.ASCII.GetBytes(
                                    string.Format(
                                        "* That's it. Good job. It took you {0} guesses. Thanks for playing.",
                                        tries));
                            conn.Send(correct, 0, correct.Length, 0);
                            break;
                        }
                    }
                }
            }

            conn.Close();
        }
    }
}
