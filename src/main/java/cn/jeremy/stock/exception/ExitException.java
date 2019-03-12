package cn.jeremy.stock.exception;

/**
 * 自定义的退出异常类
 *
 * @author fengjiangtao
 * @data 2017年12月1日
 */
public class ExitException extends RuntimeException
{
    private static final long serialVersionUID = 4255912611196176083L;
    
    private int code;
    
    public ExitException()
    {
        super();
    }
    
    public ExitException(int code, String message, Throwable cause)
    {
        super(message, cause);
        this.code = code;
    }
    
    public ExitException(int code, String message)
    {
        super(message);
        this.code = code;
    }
    
    public ExitException(Throwable cause)
    {
        super(cause);
    }
    
    public int getCode()
    {
        return code;
    }
    
    public void setCode(int code)
    {
        this.code = code;
    }
    
}
