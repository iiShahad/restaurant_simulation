import java.time.LocalTime;

class Customer implements Comparable<Customer> {
    private int id;
    private LocalTime arrivalTime;
    private String order;

    public Customer(int id, LocalTime arrivalTime, String order){
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.order = order;
    }

    public int getId(){
        return id;
    }

    public LocalTime getArrivalTime(){
        return arrivalTime;
    }

    public String getOrder(){
        return order;
    }

    public String toString(){
        return "Customer ID: " + id + " Arrival Time: " + arrivalTime + " Order: " + order;
    }

    @Override
    public int compareTo(Customer o) {
        //return the earliest arrival time
        return this.arrivalTime.compareTo(o.arrivalTime);
    }
}