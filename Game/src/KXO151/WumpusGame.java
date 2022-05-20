package KXO151;
/**
 * KXO151 Assignment 3, 2022
 * <p>
 * Hunt the Wumpus -- Organiser Class
 * <p>
 * WumpusGame.java
 *
 * @author: <<INSERT YOUR NAME HERE>>
 */


import java.util.Scanner;


public class WumpusGame {
    //final instances variables
    public final int IMPOSSIBLE = -1;    // operation unsuccessful because it was not allowed
    public final int SUCCESS = 0;        // operation successful
    public final int EATEN = 1;            // operation unsuccessful because player found by wumpus
    public final int FELL = 2;            // operation unsuccessful because player fell in pit
    public final int FAILURE = 3;        // operation unsuccessful for reason other than above two reasons
    private final int[] RIGHT_CAVE = {3, 0, 1, 2, 5, 6, 7, 4};    // caves to right
    private final int[] LEFT_CAVE = {1, 2, 3, 0, 7, 4, 5, 6};    // caves to left
    private final int[] MID_CAVE = {4, 5, 6, 7, 0, 1, 2, 3};    // caves to front

    private boolean tracing;//tracing option
    private Scanner scanner;//scanner instance
    private WumpusBot wumpusBot;//WumpusBot instance
    private int totalCnt;//indicate the total rounds games
    private int winCnt;//indicate the round of winning
    private int lostEaten;// lost (by being eaten)  games.
    private int lostFall;// lost (by falling in the pit) 0 games.
    private int lostQuit;//lost (due to quitting) 0 games
    private int arrowRemain;//the remaining nums of arrows
    private boolean isFirst;//whether is the first time to play

    /**
     * create a instace of WumpusGame
     */
    public WumpusGame() {
        scanner = new Scanner(System.in);
        wumpusBot = new WumpusBot();
        this.totalCnt = 0;
        this.winCnt = 0;
        this.lostEaten = 0;
        this.lostFall = 0;
        this.lostQuit = 0;
        this.isFirst = true;
        //switch tracing option on
        //switch wumpusBot tracing on
        this.setTracing(true);
        wumpusBot.setTracing(true);
        play();//start to play
    }


    /**
     * start to play the game
     */
    public void play() {
        trace("play method");
        String opt = prepareGame();
        initialGame();
        if (opt.equals("y")) {
            while (true) {
                showDetail();
                String option = getOption();
                switch (option) {
                    case "w":
                        if (!walkInto()) {
                            play();//a new round begin to play
                            return;
                        }
                        break;
                    case "s":
                        if (shoot()) {
                            play();
                            return;
                        }
                        break;
                    case "q":
                        this.totalCnt++;
                        this.lostQuit++;
                        play();
                        return;
                }
            }
        } else {
            quitMessage();
        }
    }

    /**
     * initial the  new game
     */
    public void initialGame() {
        wumpusBot.newGame();
        this.arrowRemain = 3;
    }

    /**
     * cope with all conditions about the shooting
     *
     * @return whether the player could start a new round
     */
    public boolean shoot() {
        if (decrArrow()) {
            System.out.print("Which cave would you like to shoot into?");
            int target = scanner.nextInt();
            scanner.nextLine();
            int res = wumpusBot.tryShoot(target);
            switch (res) {
                case IMPOSSIBLE:
                    System.out.println("What a waste. The arrow just bounced off the wall...");
                    return false;
                case SUCCESS:
                    System.out.println("It's a direct hit. You've killed the wumpus!");
                    this.winCnt++;
                    this.totalCnt++;
                    return true;
                case FAILURE:
                    System.out.println("Arrow missed. I guess the wumpus wasn't in there...");
                    return false;
            }
        } else {
            System.out.println("You can't shoot -- you have no arrows left!");
        }
        return false;
    }

    /**
     * @return {@code true}if the player could shoot and decrease the number of arrow
     */
    public boolean decrArrow() {
        boolean result;
        if (this.arrowRemain > 0) {
            this.arrowRemain--;
            result = true;
        } else {
            result = false;
        }
        return result;
    }


    /**
     * @return {@code "y"} or  {@code "n"} when asking whether to play the game (again)
     */
    public String prepareGame() {
        String opt;
        if (this.isFirst) {
            explain();
            this.isFirst = false;
            System.out.print("Would you like to play Hunt the Wumpus?");
            opt = scanner.nextLine();
        } else {
            System.out.print("Would you like to play Hunt the Wumpus again?");
            opt = scanner.nextLine();
        }
        return opt;
    }

    /**
     * plan to walk and meanwhile cope with the different conditions
     *
     * @return wthther the game could continue
     */
    public boolean walkInto() {
        System.out.print("Which cave would you like to walk into? ");
        int into = scanner.nextInt();
        scanner.nextLine();
        int res = wumpusBot.tryWalk(into);
        switch (res) {
            case SUCCESS:
                System.out.println("Walk successful.");
                return true;
            case FELL:
                System.out.println("You're dead. You just fell in the pit.");
                this.lostFall++;
                this.totalCnt++;
                return false;
            case EATEN:
                System.out.println("You're dead. The wumpus gotcha!");
                this.lostEaten++;
                this.totalCnt++;
                return false;
        }
        return true;
    }

    /**
     * show the details of the player
     */
    public void showDetail() {
        int cur = wumpusBot.getCurrent();
        System.out.println("\nYou are in cave #" + cur);
        System.out.println("To your left is #" + this.LEFT_CAVE[cur] +
                ", to your right is #" + this.RIGHT_CAVE[cur] +
                ", and ahead is #" + this.MID_CAVE[cur] + ".");
        System.out.println("You have " + this.arrowRemain + " arrows remaining.");
        showAround();
    }

    /**
     * show detail about the caves around the player
     */
    public void showAround() {
        if (wumpusBot.wumpusNear()) {
            System.out.println("\nYou can smell something horrible.");
        }
        if (wumpusBot.pitNear()) {
            System.out.println("\nYou feel a cold wind");
        }
    }


    /**
     * @return {@code "w","s","q"}indicate the option of the user.
     */
    public String getOption() {
        System.out.print("\nPlease choose from (W)alk, (S)hoot, or (Q)uit:");
        String opt = scanner.nextLine();
        return opt;
    }

    /**
     * explain the game
     */
    public void explain() {
        trace("explain the game");
        System.out.println("Hunt the Wumpus!");
        System.out.println("================");
        System.out.println("You have to find and shoot the wumpus and not fall in the pit");
    }


    public void setTracing(boolean onOff) {
        tracing = onOff;
    }


    /**
     * @param message print  and trace the message
     */
    public void trace(String message) {
        if (tracing) {
            System.out.println("WumpusGame: " + message);
        }
    }

    /**
     * show messages of the condition of total game
     */
    public void quitMessage() {
        System.out.println("You played a total of " + this.totalCnt + " games.");
        System.out.println("You won " + this.winCnt + " games.");
        System.out.println("You lost (by being eaten) " + lostEaten + " games");
        System.out.println("You lost (by falling in the pit) " + lostFall + " games");
        System.out.println("You lost (due to quitting) " + lostQuit + " games");
    }
}
