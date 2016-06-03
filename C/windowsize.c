// program to capture window size change signal
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <termios.h>
#include <signal.h>
#include <sys/ioctl.h>

void print_win_size();
void sig_winch_handler(int signum);

main()
{
  print_win_size();

  signal(SIGWINCH, sig_winch_handler);

  for (;;)              // wait forever
    sleep(1);
}

void print_win_size() {
  struct winsize ws;

  ioctl(STDOUT_FILENO, TIOCGWINSZ, &ws);
  printf("%d rows, %d cols\n", ws.ws_row, ws.ws_col);
}


// window size change handler
void sig_winch_handler(int signum) {
  printf("SIGWINCH: ");
  print_win_size();
}

