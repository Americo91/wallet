package astoppello.wallet.exception;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Class aClass, UUID uuid) {
        super(aClass.getName() + "with id " + uuid + " not found");
    }

    public NotFoundException(Class aClass, String name) {
        super(aClass.getName() + "with name " + name + " not found");
    }

}
