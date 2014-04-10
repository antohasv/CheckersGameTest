package gameMechanic;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import gameClasses.Field;
import resource.GameSettings;
import resource.ResourceFactory;

public class GameModule extends AbstractModule {

    private Field[][] fields;
    private GameSettings settings;

    public GameModule() {
        settings = (GameSettings) ResourceFactory.instanse().getResource(GameSession.FILE_GAME_SETTINGS);
        fields = GenerateChessBoard.generateEmptyField();
    }

    @Override
    protected void configure() {
        int whiteCount = 0;
        int blackCount = 0;

        for (int i = 0; i < settings.getFieldSize(); i++) {
            for (int j = 0; j < settings.getFieldSize(); j++) {
                if (this.fields[i][j].getType() == Field.Checker.white) {
                    whiteCount++;
                }

                if (this.fields[i][j].getType() == Field.Checker.black) {
                    blackCount++;
                }
            }
        }

        bind(Field[][].class).toInstance(this.fields);
        bindConstant().annotatedWith(Names.named(GameSession.NAME_WHITE_QUANTITY)).to(whiteCount);
        bindConstant().annotatedWith(Names.named(GameSession.NAME_BLACK_QUANTITY)).to(blackCount);
    }


    public void setField(Field.Checker type, boolean king, int x, int y) {
        this.fields[y][x].setType(type);
        if (king) {
            this.fields[y][x].makeKing();
        }
    }
}
