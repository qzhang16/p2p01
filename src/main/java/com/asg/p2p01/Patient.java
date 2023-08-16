package com.asg.p2p01;

import java.io.Serializable;

public class Patient implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private String insuranceProvider;
    private double copay;
    private double topay;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getInsuranceProvider() {
        return insuranceProvider;
    }
    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }
    public double getCopay() {
        return copay;
    }
    public void setCopay(double copay) {
        this.copay = copay;
    }
    public double getTopay() {
        return topay;
    }
    public void setTopay(double topay) {
        this.topay = topay;
    }
    

}
