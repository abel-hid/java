package labs.pm.data;

public class ProductManagerException extends Exception 
{
   

    public ProductManagerException(String message) {
        super(message);
    }

    public ProductManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductManagerException(Throwable cause) {
        super(cause);
    }

    // public ProductManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    //     super(message, cause, enableSuppression, writableStackTrace);
    // }
}