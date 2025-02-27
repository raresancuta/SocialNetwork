package ubb.scs.map.socialnetwork.domain;

public class LoginCredentials extends Entity<Long>{
    private String email;
    private String password;
    public LoginCredentials(Long id,String email, String password) {
        this.email = email;
        this.password = password;
        this.setId(id);
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LoginCredentials) {
            LoginCredentials a = (LoginCredentials) obj;
            return email.equals(a.getEmail()) && password.equals(a.getPassword()) && this.getId().equals(a.getId());
        }
        return false;
    }
}
