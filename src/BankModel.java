import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

public final class BankModel {
    public interface Listener {
        void modelChanged();
    }

    public record Transaction(String type, BigDecimal amount, LocalDateTime date,
            BigDecimal resultingBalance) {
    }

    private final Map<AccountId, String> holders = new LinkedHashMap<>();
    private final Map<AccountId, BigDecimal> balances = new HashMap<>();
    private final Map<AccountId, List<Transaction>> history = new HashMap<>();
    private final List<Listener> listeners = new ArrayList<>();

    public BankModel() {
        add("100001", "Mohammed", "1000.00");
        add("100002", "Abdualaziz", "500.00");
        add("100003", "Rayan", "1000000.00");
        add("100004", "Bassam", "1000.00");
    }

    private void add(String number, String holder, String balance) {
        AccountId id = new AccountId(number);
        holders.put(id, holder);
        balances.put(id, new BigDecimal(balance));
        history.put(id, new ArrayList<>());
    }

    public void addListener(Listener listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (Listener l : List.copyOf(listeners))
            l.modelChanged();
    }

    public List<AccountId> accounts() {
        return List.copyOf(holders.keySet());
    }

    private void requireAccount(AccountId id) {
        if (!balances.containsKey(id))
            throw new IllegalArgumentException("Unknown account.");
    }

    public String holder(AccountId id) {
        requireAccount(id);
        return holders.get(id);
    }

    public BigDecimal balance(AccountId id) {
        requireAccount(id);
        return balances.get(id);
    }

    public List<Transaction> history(AccountId id) {
        requireAccount(id);
        return List.copyOf(history.get(id));
    }

    private BigDecimal validAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero.");
        try {
            return amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Use at most two decimal places.");
        }
    }

    private void record(AccountId id, String type, BigDecimal amount, LocalDateTime time) {
        history.get(id).add(new Transaction(type, amount, time, balances.get(id)));
    }

    public void deposit(AccountId id, BigDecimal amount) {
        requireAccount(id);
        amount = validAmount(amount);
        balances.put(id, balance(id).add(amount));
        record(id, "Deposit", amount, LocalDateTime.now());
        notifyListeners();
    }

    public void withdraw(AccountId id, BigDecimal amount) {
        requireAccount(id);
        amount = validAmount(amount);
        if (balance(id).compareTo(amount) < 0)
            throw new IllegalArgumentException("Insufficient funds.");
        balances.put(id, balance(id).subtract(amount));
        record(id, "Withdrawal", amount, LocalDateTime.now());
        notifyListeners();
    }

    public void transfer(AccountId from, AccountId to, BigDecimal amount) {
        requireAccount(from);
        requireAccount(to);
        amount = validAmount(amount);
        if (from.equals(to))
            throw new IllegalArgumentException("Select a different destination account.");
        if (balance(from).compareTo(amount) < 0)
            throw new IllegalArgumentException("Insufficient funds.");
        // Validate both accounts and the amount before changing either balance.
        BigDecimal debit = balance(from).subtract(amount), credit = balance(to).add(amount);
        LocalDateTime time = LocalDateTime.now();
        balances.put(from, debit);
        balances.put(to, credit);
        record(from, "Transfer to " + to, amount, time);
        record(to, "Transfer from " + from, amount, time);
        notifyListeners(); // One event after the entire transfer is complete.
    }
}
