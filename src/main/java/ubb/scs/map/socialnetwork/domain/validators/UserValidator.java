package ubb.scs.map.socialnetwork.domain.validators;

import ubb.scs.map.socialnetwork.domain.User;


public class UserValidator implements Validator<User> {

    @Override
    public void validate(User entity) throws ValidationException {
        String errors = "";

        if (entity.getFirstName().equals("")) {
            errors += "Invalid First Name\n";
        }

        if (entity.getLastName().equals("")) {
            errors += "Invalid Last Name\n";
        }

        if (errors.length() > 0) {
            throw new ValidationException(errors);
        }
    }
}

