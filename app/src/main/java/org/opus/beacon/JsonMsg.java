package org.opus.beacon;

public class JsonMsg {
    public static class CreateAccountRequest {
        public CreateAccountRequest(String username, String token) {
            setUsername(username);
            setToken(token);
        }

        private String _username;
        private String _token;
        public String getUsername() {return _username;}
        public void setUsername(String u) {_username = u;}
        public String getToken() {return _token;}
        public void setToken(String t) {_token = t;}
    }

    public static class CreateAccountResponse {
        public CreateAccountResponse() {}

        private int _id;
        private String _secret;
        public int getId() {return _id;}
        public void setId(int id) {_id = id;}
        public String getSecret() {return _secret;}
        public void setSecret(String secret) {_secret = secret;}
    }

    public static class ErrorResp {
        public ErrorResp() {}

        private String _error;
        public String getError() {return _error;}
        public void setError(String error) {_error = error;}
    }
}
