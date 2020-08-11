package mobi.idealabs.ads.report;

import com.mopub.common.util.Reflection;

public class EventInterFaceFactory {
    public static EventInterface createEventInterface(String adUnitID, String requestId) {
        try {
            return (EventInterface) Reflection.instantiateClassWithConstructor("mobi.idealabs.ads.core.network.TrackEvent", Object.class,
                    new Class[]{String.class, String.class},
                    new Object[]{adUnitID, requestId});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
