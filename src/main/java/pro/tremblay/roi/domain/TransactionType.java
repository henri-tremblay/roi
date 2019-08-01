package pro.tremblay.roi.domain;

import java.math.BigDecimal;
import java.util.Map;

public enum TransactionType {
    /** Sell securities for cash */
    sell {
        @Override
        public void revertTransaction(Transaction transaction, Map<String, Account> accountsPerName) {
            Account account = accountsPerName.get(transaction.getAccountName());
            if(account == null) {
                return; // account not found for some reason...
            }

            // first remove the selling price from the cash account
            subtractFromCashPosition(transaction, account);
            // the add back the quantity to the correct position
            subtractFromSecurityPosition(transaction, account);
        }
    },
    /** Buy securities with cash */
    buy {
        @Override
        public void revertTransaction(Transaction transaction, Map<String, Account> accountsPerName) {
            Account account = accountsPerName.get(transaction.getAccountName());
            if(account == null) {
                return; // account not found for some reason...
            }

            // first add the buying price from the cash account
            subtractFromCashPosition(transaction, account);
            // the rollback back the quantity of the correct position
            subtractFromSecurityPosition(transaction, account);
        }
    },
    /** Add cash to the account */
    deposit,
    /** Remove money from the account */
    withdrawal;

    /**
     * Revert the transaction on the account to put the account in the state it was before the transaction.
     *
     * @param transaction transaction to revert
     * @param accountsPerName map of all accounts per name
     */
    public void revertTransaction(Transaction transaction, Map<String, Account> accountsPerName) {
        Account account = accountsPerName.get(transaction.getAccountName());
        subtractFromCashPosition(transaction, account); // the cash amount will be native on
    }

    private static void subtractFromCashPosition(Transaction transaction, Account account) {
        BigDecimal amount = transaction.getAmount();

        // rollback the amount involved in the transaction if there are any
        if(amount != null) {
            BigDecimal cash = account.getCash();
            account.cash(cash.subtract(amount));
        }
    }

    private static void subtractFromSecurityPosition(Transaction transaction, Account account) {
        // Search the position, it might not exist if the position was sold so we create it if needed
        Position searchedPosition = searchPosition(transaction, account);

        if (searchedPosition != null) {
            searchedPosition.quantity(searchedPosition.getQuantity() - transaction.getQuantity());
        } else {
            searchedPosition = new Position();
            searchedPosition.quantity(-transaction.getQuantity());

            Security security = transaction.getSecurity();
            searchedPosition.security(security);
            account.addPosition(searchedPosition);
        }
    }

    private static Position searchPosition(Transaction transaction, Account account) {
        Position searchedPosition = null;
        for(Position pos : account.getPositions()) {
            if (pos.getSecurity() != null) {
                // Check for null symbol on position - most probably private company
                if (pos.getSecurity().getSymbol() == null) {
                    continue;
                }
                if (transaction.getSecurity() != null) {
                    if (pos.getSecurity().getSymbol().equals(transaction.getSecurity().getSymbol())) {
                        searchedPosition = pos;
                        break;
                    }
                } else {
                    // Check against a position we created earlier in substractFromSecurityPosition
                    if (pos.getSecurity().getSymbol().equals(transaction.getSecurity().getSymbol())) {
                        searchedPosition = pos;
                        break;
                    }
                }
            }
        }
        return searchedPosition;
    }

}
