
import java.util.Random;

class Waiter extends Thread {

    //Constructor --------------------------------------------------------------------
    private final int id;
    private final CircularBuffer<Order> readyOrders;

    public Waiter(int id, CircularBuffer<Order> readyOrders) {
        this.id = id;
        this.readyOrders = readyOrders;
    }

    //Global variables --------------------------------------------------------------
    static CustomSemaphore mutex = new CustomSemaphore(1);
    boolean endShift = false;

    //Thread run method -------------------------------------------------------------
    /*
    Steps:
    1. The loop continues until either `endShift` is true or the `orderBuffer` is empty.
    - This ensures that the Chef works until the end of their shift or until all orders have been processed.

    2. Acquire the `mutex` lock to ensure mutual exclusion when accessing the shared `orderBuffer`.
    - This prevents other threads from modifying the `orderBuffer` concurrently, ensuring thread safety.

    3. Check if the `orderBuffer` is empty:
    - If it is empty, release the `mutex` and continue to the next iteration of the loop (waiting for new orders).

    4. If the buffer is not empty, remove an order from the `orderBuffer` and store it in `orderMeal`.
    - The Chef retrieves an order to prepare, and the order’s details are logged.

    5. After releasing the mutex, simulate the time taken to prepare the meal by calling `Thread.sleep()` with the meal’s preparation time (in minutes).
    - This models the time it takes the Chef to cook the meal.

    6. Once the meal is prepared, log the completion, and mark the order as ready by calling `orderMeal.markOrderReady(this.id)`.
    - This notifies that the meal is ready for the customer.

    7. In the `finally` block, check if the `mutex` lock has been acquired and ensure it is released if necessary, avoiding a deadlock.
     */
    @Override
    public void run() {
        while (!endShift || !readyOrders.isEmpty()) { //Continue until the end of the shift or all orders are processed
            try {
                try {
                    mutex.acquire(); //Acquire the mutex lock
                    //retrieve order from buffer
                    Order orderMeal = (Order) readyOrders.remove();
                    if (orderMeal == null) {
                        mutex.release();
                        break;
                    }

                    System.out.println("Waiter " + id + " is serving " + orderMeal.getMealName() + " for Customer " + orderMeal.getCustomerId());

                    mutex.release(); //Release the mutex lock

                    //simulate meal serving time
                    Thread.sleep((long) (1000 * generateRandomServingTime() * 60 * 0.1));
                    System.out.println("Waiter " + id + " has served " + orderMeal.getMealName() + " for Customer " + orderMeal.getCustomerId());

                    //mark order as ready and notify customer
                    orderMeal.markOrderServed(this.id);
                } finally {
                    //Ensure mutex is released in case of exception
                    if (mutex.getCurrentPermits() == 0) {
                        mutex.release();
                    }
                }
            } catch (Exception e) {
                System.err.println("Exception in run method: " + e.getMessage());
            }
        }

    }

    //endShift method --------------------------------------------------------------
    //This method is used to end the shift of the chef when all the customers have been served
    public void endShift() {
        System.out.println("Waiter " + id + " has ended the shift");
        endShift = true;
        readyOrders.endShift();
    }

    //This method is used to generate a random serving time time between 5 and 10 minutes
    public int generateRandomServingTime() {
        Random random = new Random();
        int min = 5;
        int max = 10;
        int num = ((random.nextInt((max - min) + 1) + min));
        return num;
    }
}
