package service;

import com.spaceoperators.dao.SessionDao;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionDao sessionDao;

    public SessionService(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    public void createSession(String idGame, String idPlayer, String name_player, Boolean isReady) {
        //String sessionId = UUID.randomUUID().toString(); // Génère un ID unique
        // Insère la session avec l'ID de jeu et l'ID joueur (idPlayer peut être null)
        sessionDao.insertSession(idGame, idPlayer, name_player, isReady);
    }
}
