package org.opus.beacon;

/*
* Error codes are as follows
*
*   - 30: Connection Error
*   - 31: Protocol Error (eg http, multipart)
*   - 32: Json Format Error
*
*   Documentation for the rest of the error codes lives with the backend,
*   but these rules are followed:
*
*   - The range 30-49 is reserved for server-side errors that the user
*       *should* be informed about.
*   - The range 50-69 is reserved for server errors that the user should
*       not be notified about.
 */

public class RestException extends Exception {
    private int errorCode;

    public static int ConnectionError = 30;
    public static int ProtocolError = 31;
    public static int JsonError = 32;
    public static int DatabaseError = 40;
    public static int ServerError = 41;
    public static int ExternalServiceError = 42;
    public static int NoAccountFoundError = 50;
    public static int UsernameExistsError = 51;
    public static int UnspecifiedError = 99;

    public RestException(int _errorCode, String msg) {
        super(msg);
        errorCode = _errorCode;
    }

    public int getCode() {
        return errorCode;
    }

    public boolean shouldInformUser() {
        return errorCode >= 30 && errorCode <= 49;
    }
}
