import java.util.*;

public class BuddySystem {
    // to track free and allocated memory blocks
    private TreeMap<Integer, List<Integer>> freeBlocks = new TreeMap<>();
    private HashMap<Integer, Integer> allocatedBlocks = new HashMap<>();

    // Initialize the memory pool
    public BuddySystem(int totalMemorySize) {
        freeBlocks.put(totalMemorySize, new ArrayList<>(Collections.singletonList(0)));
    }

    // Method to allocate memory
    public void allocateMemory(int size) {
        // Round off the requested size to the next power of two
        int requiredSize = nextPowerOfTwo(size);
        Integer blockSize = freeBlocks.ceilingKey(requiredSize);

        // Check if sufficient memory is available
        if (blockSize == null) {
            System.out.println("Error: Not enough memory to allocate " + size + " KB!");
            return;
        }

        // Find a block and allocate memory
        int blockAddress = freeBlocks.get(blockSize).remove(0);
        if (freeBlocks.get(blockSize).isEmpty()) {
            freeBlocks.remove(blockSize);
        }

        // Split blocks until the requested size is reached
        while (blockSize > requiredSize) {
            blockSize /= 2;
            int buddyAddress = blockAddress + blockSize;
            freeBlocks.computeIfAbsent(blockSize, k -> new ArrayList<>()).add(buddyAddress);
        }

        // Record the allocated block
        allocatedBlocks.put(blockAddress, requiredSize);
        System.out.println("Memory allocated: " + requiredSize + " KB at address " + blockAddress);
    }

    // Method to free memory
    public void freeMemory(int address) {
        Integer blockSize = allocatedBlocks.remove(address);
        if (blockSize == null) {
            System.out.println("Error: Invalid address " + address);
            return;
        }

        // Merge buddies if possible
        while (true) {
            int buddyAddress = address ^ blockSize;
            List<Integer> buddyList = freeBlocks.get(blockSize);
            if (buddyList != null && buddyList.remove((Integer) buddyAddress)) {
                if (buddyList.isEmpty()) {
                    freeBlocks.remove(blockSize);
                }
                address = Math.min(address, buddyAddress);
                blockSize *= 2;
            } else {
                break;
            }
        }

        // Add the merged block to the free list
        freeBlocks.computeIfAbsent(blockSize, k -> new ArrayList<>()).add(address);
        System.out.println("Memory freed: Block starting at address " + address + " of size " + blockSize + " KB");
    }

    // Method to display the current memory state
    public void displayMemoryState() {
        System.out.println("Free Blocks:");
        for (Map.Entry<Integer, List<Integer>> entry : freeBlocks.entrySet()) {
            System.out.println(entry.getKey() + " KB: " + entry.getValue());
        }
        System.out.println("Allocated Blocks:");
        for (Map.Entry<Integer, Integer> entry : allocatedBlocks.entrySet()) {
            System.out.println("Address: " + entry.getKey() + ", Size: " + entry.getValue() + " KB");
        }
    }

    // Utility method to calculate the next power of two
    private int nextPowerOfTwo(int n) {
        int power = 1;
        while (power < n) {
            power *= 2;
        }
        return power;
    }

    // Main method 
    public static void main(String[] args) {
        BuddySystem manager = new BuddySystem(1024); // Initialize with 1024 KB

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nBuddy System Memory Manager:");
            System.out.println("1. Allocate Memory");
            System.out.println("2. Free Memory");
            System.out.println("3. Display Memory State");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter memory size to allocate (KB): ");
                    int size = scanner.nextInt();
                    manager.allocateMemory(size);
                    break;
                case 2:
                    System.out.print("Enter starting address of block to free: ");
                    int address = scanner.nextInt();
                    manager.freeMemory(address);
                    break;
                case 3:
                    manager.displayMemoryState();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
