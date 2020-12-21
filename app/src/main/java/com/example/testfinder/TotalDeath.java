package com.example.testfinder;

class TotalDeath {
    int dirDeath, indirDeath, dirInj, indiirInj;
    public TotalDeath(int ddeath, int ideath, int dinj, int iinj) {
        this.dirDeath = ddeath;
        this.indirDeath = ideath;
        this.dirInj = dinj;
        this.indiirInj = iinj;
    }

    public int getDDeath() {
        return dirDeath;
    }

    public int getIDeath() {
        return indirDeath;
    }

    public int getDInj() {
        return dirInj;
    }

    public int getIInj() {
        return indiirInj;
    }
}
