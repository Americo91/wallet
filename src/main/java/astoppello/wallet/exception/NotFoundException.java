package astoppello.wallet.exception;

import java.util.UUID;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Class aClass, UUID uuid) {
        super(aClass.getSimpleName() + " with id " + uuid + " not found");
    }

    public NotFoundException(Class aClass, String name) {
        super(aClass.getSimpleName() + " with name " + name + " not found");
    }

}
