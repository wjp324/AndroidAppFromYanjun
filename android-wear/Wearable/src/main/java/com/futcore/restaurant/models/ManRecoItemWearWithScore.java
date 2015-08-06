package com.futcore.restaurant.models;

public class ManRecoItemWearWithScore extends ManRecoItemWear
{
    private long score;

    public ManRecoItemWearWithScore(ManRecoItemWear reco, long score)
    {
        super(reco.getRecoId(), reco.getManItem(), reco.getModelConfig(), reco.getRecoDuration(), reco.getRecoDate());
        //        this = reco;
        this.score = score;
        //        super.ManRecoItemWear();
    }

    public long getScore()
    {
        return score;
    }

    public void setScore(long score)
    {
        this.score = score;
    }
}

