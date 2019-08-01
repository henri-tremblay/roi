package pro.tremblay.roi.service;

public class MessageService extends DependencyService {

    public MessageService() {
    }

    public MessageService(boolean isThrottling) {
        super(isThrottling);
    }

    /**
     * Returned the message for this key in the user language
     *
     * @param key message key as found in the translation files
     * @return translated message
     */
    public String getMessage(String key) {
        return key;
    }
}
