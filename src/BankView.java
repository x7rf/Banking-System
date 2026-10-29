import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/** View observes the model but never changes its banking state. */
public final class BankView extends JPanel implements BankModel.Listener {
    final JComboBox<AccountId> account = new JComboBox<>(), destination = new JComboBox<>();
    final JTextField amount = new JTextField(12);
    final JButton deposit = new JButton("Deposit"), withdraw = new JButton("Withdraw"), transfer = new JButton("Transfer");
    final JLabel details = new JLabel(), status = new JLabel("Ready. Select an account and enter an amount.");
    private final BankModel model;
    private final DefaultTableModel rows = new DefaultTableModel(new String[]{"Type", "Amount (SAR)", "Date and time", "Balance (SAR)"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    public BankView(BankModel model) {
        this.model = model;
        setLayout(new BorderLayout(12,12)); setBorder(BorderFactory.createEmptyBorder(22,22,22,22));
        JPanel header = new JPanel(new GridLayout(0,1,6,6));
        JLabel title = new JLabel("Banking System"); title.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.add(title); header.add(new JLabel("Currency: SAR"));
        JPanel selection = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (AccountId id : model.accounts()) { account.addItem(id); destination.addItem(id); }
        destination.setSelectedIndex(1);
        selection.add(new JLabel("Account:")); selection.add(account); header.add(selection); header.add(details);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(new JLabel("Amount:")); actions.add(amount); actions.add(deposit); actions.add(withdraw);
        actions.add(new JLabel("Transfer to:")); actions.add(destination); actions.add(transfer); header.add(actions);
        add(header, BorderLayout.NORTH);
        JTable table = new JTable(rows); table.setRowHeight(28);
        JScrollPane scroll = new JScrollPane(table); scroll.setBorder(BorderFactory.createTitledBorder("Transaction history"));
        add(scroll, BorderLayout.CENTER); add(status, BorderLayout.SOUTH);
        model.addListener(this); modelChanged();
    }
    public AccountId selectedAccount() { return (AccountId) account.getSelectedItem(); }
    public void message(String text, boolean error) {
        status.setForeground(error ? new Color(170,25,25) : new Color(15,100,55)); status.setText(text);
    }
    @Override public void modelChanged() {
        AccountId id = selectedAccount();
        details.setText("Account: " + id + "     Holder: " + model.holder(id) + "     Balance: SAR " + model.balance(id).toPlainString());
        rows.setRowCount(0);
        for (BankModel.Transaction t : model.history(id)) rows.addRow(new Object[]{t.type(), t.amount().toPlainString(),
            t.date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), t.resultingBalance().toPlainString()});
    }
}
