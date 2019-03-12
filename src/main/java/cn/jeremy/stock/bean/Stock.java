package cn.jeremy.stock.bean;

import cn.jeremy.stock.tools.StringTools;

/**
 * 股票
 *
 * @author kugoufeng
 * @date 2017/12/22 上午 11:47
 */
public class Stock
{
    /**
     * 股票所属行业名称
     */
    private String plateName;

    /**
     * 股票所属行业占比
     */
    private int platePer;

    /**
     * 股票名称
     */
    private String stockName;

    /**
     * 股票代号
     */
    private String stockNum;

    /**
     * 股票价格
     */
    private int stockPrice;

    /**
     * 股票涨跌
     */
    private int stockChg;

    /**
     * 股票占比
     */
    private int stockPer;

    /**
     * 股票数量
     */
    private int stockPositionNum;

    public Stock()
    {
        super();
    }

    public Stock(String plateName, int platePer, String stockName, String stockNum, int stockPrice, int stockChg,
        int stockPer, int stockPositionNum)
    {
        this.plateName = plateName;
        this.platePer = platePer;
        this.stockName = stockName;
        this.stockNum = stockNum;
        this.stockPrice = stockPrice;
        this.stockChg = stockChg;
        this.stockPer = stockPer;
        this.stockPositionNum = stockPositionNum;
    }

    public Stock(String stockMessage)
    {
        String[] split = stockMessage.split("\\|");
        this.plateName = split[0];
        this.platePer = Integer.parseInt(split[1]);
        this.stockName = split[2];
        this.stockNum = split[3];
        this.stockPrice = Integer.parseInt(split[4]);
        this.stockChg = Integer.parseInt(split[5]);
        this.stockPer = Integer.parseInt(split[6]);
        this.stockPositionNum = Integer.parseInt(split[7]);
    }

    public String getPlateName()
    {
        return plateName;
    }

    public void setPlateName(String plateName)
    {
        this.plateName = plateName;
    }

    public int getPlatePer()
    {
        return platePer;
    }

    public void setPlatePer(int platePer)
    {
        this.platePer = platePer;
    }

    public String getStockName()
    {
        return stockName;
    }

    public void setStockName(String stockName)
    {
        this.stockName = stockName;
    }

    public String getStockNum()
    {
        return stockNum;
    }

    public void setStockNum(String stockNum)
    {
        String[] numAndPrize = stockNum.split(" ");
        if (numAndPrize[0].startsWith("SZ") || numAndPrize[0].startsWith("SH"))
        {
            this.stockNum = numAndPrize[0].substring(2);
        }
        String[] prizeAndChg = numAndPrize[1].split("\\(");
        this.stockPrice = StringTools.yuanToFen(prizeAndChg[0]);
        String[] chgString = prizeAndChg[1].split("%");
        String chgSymbol = chgString[0].substring(0, 1);
        int i = -1;
        if ("+".equals(chgSymbol))
        {
            i = 1;
        }

        this.stockChg = i * StringTools.yuanToFen(chgString[0].substring(1));

    }

    public int getStockPrice()
    {
        return stockPrice;
    }

    public void setStockPrice(int stockPrice)
    {
        this.stockPrice = stockPrice;
    }

    public int getStockChg()
    {
        return stockChg;
    }

    public void setStockChg(int stockChg)
    {
        this.stockChg = stockChg;
    }

    public int getStockPer()
    {
        return stockPer;
    }

    public void setStockPer(int stockPer)
    {
        this.stockPer = stockPer;
    }

    public int getStockPositionNum()
    {
        return stockPositionNum;
    }

    public void setStockPositionNum(int stockPositionNum)
    {
        this.stockPositionNum = stockPositionNum;
    }

    /**
     * 判断股票的持仓是否相同
     *
     * @param stock
     * @author fengjiangtao
     */
    public int equals(Stock stock)
    {
        if (null == stock)
        {
            return this.stockPer;
        }
        return this.stockPer - stock.getStockPer();

    }

    @Override
    public String toString()
    {
        return plateName + "|" + platePer + "|" + stockName + "|" + stockNum + "|" + stockPrice + "|" + stockChg + "|"
            + stockPer + "|" + stockPositionNum;
    }

}
