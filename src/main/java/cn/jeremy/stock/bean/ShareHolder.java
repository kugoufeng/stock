package cn.jeremy.stock.bean;

public class ShareHolder
{
    
    private String name;
    
    private String code;
    
    private String mkCode;
    
    public ShareHolder()
    {
        super();
    }
    
    public ShareHolder(String name, String code, String mkCode)
    {
        this.name = name;
        this.code = code;
        this.mkCode = mkCode;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String getMkCode()
    {
        return mkCode;
    }
    
    public void setMkCode(String mkCode)
    {
        this.mkCode = mkCode;
    }
    
    @Override
    public String toString()
    {
        return name + "|" + code + "|" + mkCode;
    }
    
}
