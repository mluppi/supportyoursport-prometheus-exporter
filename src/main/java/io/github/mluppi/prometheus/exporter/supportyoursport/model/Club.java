package io.github.mluppi.prometheus.exporter.supportyoursport.model;

public class Club {

    private String id;
    private String name;
    private int rank;
    private int voucherCount;
    private double balance = 0;

    public Club(final String id, final String name, final int rank, final int voucherCount) {
        this.id = id;
        this.name = name;
        this.rank = rank;
        this.voucherCount = voucherCount;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public int getVoucherCount() {
        return voucherCount;
    }

    public void setVoucherCount(final int voucherCount) {
        this.voucherCount = voucherCount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(final double balance) {
        this.balance = balance;
    }
}
