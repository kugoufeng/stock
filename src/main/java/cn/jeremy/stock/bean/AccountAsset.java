package cn.jeremy.stock.bean;

/**
 * 详情资产账户
 *
 * @author kugoufeng
 * @date 2017/12/22 下午 6:33
 */
public class AccountAsset
{
    /**
     * 可用余额
     */
    private int availableBalance;

    /**
     * 资金余额
     */
    private int fundBalance;

    /**
     * 冻结余额
     */
    private int blockedBalance;

    /**
     * 可取余额
     */
    private int desirableBalance;

    /**
     * 总资产
     */
    private int totalBalance;

    /**
     * 证券市值
     */
    private int marketValueOfSecurities;

    public AccountAsset()
    {
        super();
    }

    public AccountAsset(int availableBalance, int fundBalance, int blockedBalance, int desirableBalance,
        int totalBalance, int marketValueOfSecurities)
    {
        this.availableBalance = availableBalance;
        this.fundBalance = fundBalance;
        this.blockedBalance = blockedBalance;
        this.desirableBalance = desirableBalance;
        this.totalBalance = totalBalance;
        this.marketValueOfSecurities = marketValueOfSecurities;
    }

    public int getAvailableBalance()
    {
        return availableBalance;
    }

    public void setAvailableBalance(int availableBalance)
    {
        this.availableBalance = availableBalance;
    }

    public int getFundBalance()
    {
        return fundBalance;
    }

    public void setFundBalance(int fundBalance)
    {
        this.fundBalance = fundBalance;
    }

    public int getBlockedBalance()
    {
        return blockedBalance;
    }

    public void setBlockedBalance(int blockedBalance)
    {
        this.blockedBalance = blockedBalance;
    }

    public int getDesirableBalance()
    {
        return desirableBalance;
    }

    public void setDesirableBalance(int desirableBalance)
    {
        this.desirableBalance = desirableBalance;
    }

    public int getTotalBalance()
    {
        return totalBalance;
    }

    public void setTotalBalance(int totalBalance)
    {
        this.totalBalance = totalBalance;
    }

    public int getMarketValueOfSecurities()
    {
        return marketValueOfSecurities;
    }

    public void setMarketValueOfSecurities(int marketValueOfSecurities)
    {
        this.marketValueOfSecurities = marketValueOfSecurities;
    }

    @Override
    public String toString()
    {
        return availableBalance + "|" + fundBalance + "|" + blockedBalance + "|" + desirableBalance + "|" + totalBalance
            + "|" + marketValueOfSecurities;
    }

}
