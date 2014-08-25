package com.metyou.cloud;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import com.metyou.cloud.entity.AppUser;
import com.metyou.cloud.entity.EncounterEvent;
import com.metyou.cloud.entity.SocialIdentity;
import com.metyou.cloud.entity.EncounterInfo;
import com.metyou.cloud.pojos.UserEncountered;
import com.metyou.cloud.pojos.UsersBatch;
import com.metyou.cloud.pojos.UsersRequest;

import java.io.IOException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import static com.metyou.cloud.OfyService.ofy;

/**
 * Created by mihai on 8/7/14.
 */

@Api(name = "services",
     version = "v1",
     namespace = @ApiNamespace(ownerDomain = "cloud.metyou.com", ownerName = "cloud.metyou.com", packagePath=""),
     scopes = {Constants.EMAIL_SCOPE},
     clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID},
     audiences = {Constants.ANDROID_AUDIENCE}
)

public class MetYou {

    static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    static AppUser myKey;
    Logger logger = Logger.getLogger("services");
    static AppUser[] mockUsers;

    static {
        //check if users are stored
        mockUsers = new AppUser[100];
        SocialIdentity exists = ofy().load().type(SocialIdentity.class)
                .filter("email", "yahim91@gmail.com").first().now();

        if (exists == null) {
            //register facebook user
            Logger logger = Logger.getLogger("session");
            logger.info("No users, setting up mock users");

            myKey = registerMockUser("yahim91@gmail.com", "870448222985160", "mihai");
            mockUsers[0] = registerMockUser("usclgsv_thurnsky_1408622681@tfbnw.net", "1539153762970955", "Nancy");
            mockUsers[1] = registerMockUser("jmowgeq_valtchanovsen_1408622682@tfbnw.net", "262413737287320", "Lisa");
            mockUsers[2] = registerMockUser("atnlvbo_sadanwitz_1408622681@tfbnw.net", "287039748164447", "Carol");
            mockUsers[3] = registerMockUser("kvsptpa_vijayvergiyason_1408622680@tfbnw.net", "288998704620448", "James");
            mockUsers[4] = registerMockUser("tvulafw_carrierosky_1408622668@tfbnw.net", "1496907407215719", "Dorothy");
            mockUsers[5] = registerMockUser("icyizsy_letuchysen_1408622667@tfbnw.net", "338071993025644", "David");
            mockUsers[6] = registerMockUser("pvhhyqp_fallerwitz_1408622667@tfbnw.net", "1473110812945394", "David");
            mockUsers[7] = registerMockUser("inpfvuu_listein_1408622666@tfbnw.net", "306236796222135", "Jennifer");
            mockUsers[8] = registerMockUser("eggixqd_zuckersen_1408622648@tfbnw.net", "311348959026195", "Sandra");
            mockUsers[9] = registerMockUser("wulnaxg_narayananman_1408622649@tfbnw.net", "360916687394684", "Ruth");
            mockUsers[10] = registerMockUser("uypblcj_schrockson_1408622647@tfbnw.net", "357899331025117", "Bob");
            mockUsers[11] = registerMockUser("lhmaeex_sharpeman_1408622647@tfbnw.net", "269846306555394", "Carol");

            setEncounteredUsers();
        }
    }

    @ApiMethod(
            name = "services.register",
            httpMethod = ApiMethod.HttpMethod.POST)
    public CloudResponse registerUser(SocialIdentity socialIdentity, User user) {
        if (user == null) {
            return null;
        }

        CloudResponse response = new CloudResponse();
        SocialIdentity exists = ofy().load().type(SocialIdentity.class)
                .filter("provider", socialIdentity.getProvider())
                .filter("providerId", socialIdentity.getProviderId()).first().now();

        if (exists != null) {
            response.setId(exists.getUser().id);
            return response;
        }

        logger.info("register provider " + socialIdentity.getProvider());
        logger.info("register email " + socialIdentity.getEmail());

        AppUser appUser = new AppUser();
        ofy().save().entity(appUser).now();
        socialIdentity.setUser(appUser);
        ofy().save().entity(socialIdentity).now();

        response.setId(appUser.id);
        return response;
    }

    @ApiMethod(
            name = "services.insertEncounteredUsers",
            httpMethod = ApiMethod.HttpMethod.POST)
    public void insertUsersEncountered(UsersBatch users, User user) throws OAuthRequestException,
            IOException {

        if (user == null) {
            throw new OAuthRequestException("missing user");
        }


//        ArrayList<Entity> entities = new ArrayList<Entity>();
//        for (UserEncountered userEncountered : users.getUsers()) {
//            Entity userEntity = new Entity("UserEncountered", KeyFactory.stringToKey(users.getKey()));
//            userEntity.setProperty("time", userEncountered.getTimeEncountered());
//            userEntity.setProperty("userId", userEncountered.getUserId());
//            entities.add(userEntity);
//        }
//        datastore.put(entities);

    }

    @ApiMethod(
            name = "services.getUsers",
            httpMethod = ApiMethod.HttpMethod.POST)
    public UsersBatch getUsers(UsersRequest req, User user) throws OAuthRequestException,
            IOException {

        if (user == null) {
            throw new OAuthRequestException("missing user");
        }

        Logger logger = Logger.getLogger("services");
        UsersBatch usersBatch = new UsersBatch();
        usersBatch.setReachedEnd(true);

        List<EncounterInfo> users = ofy().load()
                .type(EncounterInfo.class)
                .filter("users", Key.create(AppUser.class, req.getUserKey())).list();

        TreeMap<EncounterEvent, EncounterInfo> events = new TreeMap<EncounterEvent,EncounterInfo>(new Comparator<EncounterEvent>() {
            @Override
            public int compare(EncounterEvent o1, EncounterEvent o2) {
                return o1.date.compareTo(o2.date);
            }
        });

        logger.info("user encountered " + users.size());
        int offset = 0;
        for (EncounterInfo encounterInfo : users) {
            Long infoId = encounterInfo.getOtherUserId(req.getUserKey());

            logger.info("user encountered id " + infoId);
            EncounterEvent event = ofy().load().type(EncounterEvent.class)
                    .filter("encounterInfoRef", encounterInfo)
                    .filter("date >=", req.getBeginningDate())
                    .first().now();

            if (event == null) {
                logger.info("no events");
                continue;
            }

            if (event.date.equals(req.getBeginningDate()) && offset < req.getOffset()) {
                offset++;
                continue;
            }
            events.put(event, encounterInfo);
        }

        offset = -1;
        Date lastDate = events.firstEntry().getKey().date;
        for (Map.Entry<EncounterEvent, EncounterInfo> entry : events.entrySet()) {
            if (usersBatch.size() == req.getCount() + 1) {
                usersBatch.setReachedEnd(false);
                break;
            }

            if (lastDate.equals(entry.getKey().date)) {
                offset++;
            } else {
                offset = 0;
                lastDate = entry.getKey().date;
            }
            EncounterInfo info = entry.getValue();
            AppUser appUser = info.getOtherAppUser(req.getUserKey());
            UserEncountered userEncountered = new UserEncountered(appUser.firstName, entry.getKey().date);
            usersBatch.addUser(userEncountered);

        }

        logger.info("events " + events.size());
        return usersBatch;

//        UsersBatch usersBatch = new UsersBatch();
//        usersBatch.setReachedEnd(true);
//        ArrayList<UserEncountered> users = new ArrayList<UserEncountered>();
//        Query query = new Query("UserEncountered")/*.setAncestor(KeyFactory.stringToKey(req.getUserKey()))*/;
//        Query.Filter timeFilter = new Query.FilterPredicate(
//                "time",
//                Query.FilterOperator.LESS_THAN,
//                req.getBeginningDate()
//        );
//        query.setFilter(timeFilter);
//        PreparedQuery pq = datastore.prepare(query);
//
//        for (Entity entity : pq.asIterable()) {
//            if (users.size() == req.getCount()) {
//                usersBatch.setReachedEnd(false);
//                break;
//            }
//            UserEncountered userEncountered = new UserEncountered();
//            userEncountered.setUserId((String)entity.getProperty("userId"));
//
//            Entity userEntity = null;
//
//            String firstName = null;
//            String socialId = null;
//            try {
//                userEntity = datastore.get(KeyFactory.stringToKey((String) entity.getProperty("userId")));
//                firstName = String.valueOf(userEntity.getProperty("FirstName"));
//            } catch (EntityNotFoundException e) {
//                e.printStackTrace();
//            }
//
//            Query socialQuery = new Query("SocialIdentity").setAncestor(userEntity.getKey());
//            socialId = String.valueOf(datastore.prepare(socialQuery).asSingleEntity().getProperty("socialId"));
//            userEncountered.setFirstName(firstName);
//            userEncountered.setSocialId(socialId);
//            users.add(userEncountered);
//        }
//        usersBatch.setUsers(users);
//        return usersBatch;
    }


    public List<AppUser> getRegisteredUsers() {
        return ofy().load().type(AppUser.class).list();
    }

    static private AppUser registerMockUser(String email, String fbId, String name) {
        AppUser appUser = new AppUser();
        appUser.firstName = name;
        ofy().save().entity(appUser).now();
        SocialIdentity socialIdentity = new SocialIdentity(fbId, email, "facebook");
        socialIdentity.setFirstName(name);
        socialIdentity.setUser(appUser);
        ofy().save().entity(socialIdentity).now();
        return appUser;
    }

    static private void setEncounteredUsers() {
        Calendar c = Calendar.getInstance();
        int day = 15;

        for (int i = 0; i <= 11; i++) {
            final int j = i;
            c.set(2014, 7, day);
            day++;
            final Date date = c.getTime();

            ofy().transact(new VoidWork() {
                @Override
                public void vrun() {
                    EncounterInfo info = new EncounterInfo(myKey, mockUsers[j]);
                    ofy().save().entity(info).now();
                    EncounterEvent encounter = new EncounterEvent(info, date);
                    ofy().save().entity(encounter).now();
                }
            });
        }
    }
}