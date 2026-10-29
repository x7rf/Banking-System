# Banking System

Small Java Swing desktop application for SE-4111. Requires JDK 17 or newer. No external libraries.

## Run
From this folder:

```sh
mkdir -p build
javac -d build src/*.java
java -cp build BankApp
```

## Design
- BankModel: balances, account holders, histories and banking rules.
- BankView: GUI and Observer listener; reads model state without changing it.
- BankController: event handling, input parsing and calls to model operations.
- AccountId: immutable ADT, explicit equals/hashCode, used as map key.
- BankApp: connects the objects and opens the desktop window.

## Scope
Four sample accounts: 100001 (Mohammed, SAR 1000.00), 100002 (Abdualaziz, SAR 500.00), 100003 (Rayan, SAR 1000.00), and 100004 (Bassam, SAR 1000.00). State is held in memory and resets on restart. No real banking connection, login, database, or account creation. Transactions run on the Swing event thread; the model does not support concurrent callers. Rejected operations do not change balances or histories. Transfers validate both accounts before changing state and notify listeners once after completion.
