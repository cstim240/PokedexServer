package ca.cmpt213.model;

import ca.cmpt213.exception.TokimonCardNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ca.cmpt213.model.TokimonCard.decrementTotalTokimons;

public class TokimonCardList {
    private List<TokimonCard> tokimonCards = new ArrayList<>();
    private static final String FILE_PATH = "src/main/resources/static/tokimon.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public TokimonCardList() {
        System.out.println("Initializing TokimonCardList");
        try {
            File file = new File(FILE_PATH);
            if (file.exists()){
                mapper.writeValue(file, new ArrayList<TokimonCard>());
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        //Add sample TokimonCards
        TokimonCard tokimon1 = new TokimonCard(1, "Tokimander", TokimonCard.ElementType.FIRE, "charmander.png", 100, 50);
        TokimonCard tokimon2 = new TokimonCard(2, "Tokiurtle", TokimonCard.ElementType.WATER, "squirtle.png", 100, 50);
        TokimonCard tokimon3 = new TokimonCard(3, "Tokisaur", TokimonCard.ElementType.GRASS, "bulbasaur.png", 100, 50);
        tokimonCards.add(tokimon1);
        tokimonCards.add(tokimon2);
        tokimonCards.add(tokimon3);
        updateJsonFile();
    }

    public List<TokimonCard> getTokimonCards() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()){
                //mapper's readValue method reads the json file and converts it into a list of TokimonCard objects
                // the TypeReference class is used to specify the type of the object to be converted
                // in this case it's json file --> List<TokimonCard>
                tokimonCards = mapper.readValue(file, new TypeReference<List<TokimonCard>>(){});
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return tokimonCards;
    }

    public TokimonCard getTokimonCard(long tid){
        try {
            File file = new File(FILE_PATH);
            if (file.exists()){
                tokimonCards = mapper.readValue(file, new TypeReference<List<TokimonCard>>(){});
            }
            for (int i = 0; i < tokimonCards.size(); i++) {
                if (tokimonCards.get(i).getTid() == tid) {
                    return tokimonCards.get(i);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        throw new TokimonCardNotFoundException("TokimonCard with ID " + tid + " not found");
    }

    public void addTokimonCard(TokimonCard tokimonCard) {
        //Read existing tokimon.json file
        try{
            File file = new File(FILE_PATH);
            if (file.exists()){
                tokimonCards = mapper.readValue(file, new TypeReference<List<TokimonCard>>(){});
            }
            //Add new TokimonCard to the list
            tokimonCards.add(tokimonCard);
            // Increment the total number of Tokimons
            TokimonCard.incrementTotalTokimons();
            //Write the updated list to the json file
            updateJsonFile();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void updateTokimonCard(long tid, TokimonCard updatedTokimonCard) {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()){
                tokimonCards = mapper.readValue(file, new TypeReference<List<TokimonCard>>(){});
            }

            // Remove the TokimonCard with the specified ID
            for (int i = 0; i < tokimonCards.size(); i++) {
                if (tokimonCards.get(i).getTid() == tid) {
                    TokimonCard currentCard = tokimonCards.get(i);
                    if (updatedTokimonCard.getTid() != 0){
                        currentCard.setTid(updatedTokimonCard.getTid());
                    }

                    if (updatedTokimonCard.getName() != null){
                        currentCard.setName(updatedTokimonCard.getName());
                    }

                    if (updatedTokimonCard.getElementType() != null){
                        currentCard.setElementType(updatedTokimonCard.getElementType());
                    }

                    if (updatedTokimonCard.getImageName() != null){
                        currentCard.setImageName(updatedTokimonCard.getImageName());
                    } else if (updatedTokimonCard.getImageName() == null){
                        currentCard.setImageName("unown.png");
                    }

                    if (updatedTokimonCard.getHealthPoints() != 0){
                        currentCard.setHealthPoints(updatedTokimonCard.getHealthPoints());
                    }
                    if (updatedTokimonCard.getAttackPoints() != 0){
                        currentCard.setAttackPoints(updatedTokimonCard.getAttackPoints());
                    }

                    break;
                }
            }

            // Write the updated list to the json file
            updateJsonFile();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void deleteTokimonCard(long tid) {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()){
                tokimonCards = mapper.readValue(file, new TypeReference<>() {
                });
            }

            // Remove the TokimonCard with the specified ID
            for (int i = 0; i < tokimonCards.size(); i++) {
                if (tokimonCards.get(i).getTid() == tid) {
                    tokimonCards.remove(i);
                    decrementTotalTokimons();
                    break;
                }
            }

            // Write the updated list to the json file
            updateJsonFile();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public String getFilePath() {
        return FILE_PATH;
    }

    private void updateJsonFile(){
        try { //write the updated list of TokimonCards to the json file in the server
            mapper.writeValue(new File(FILE_PATH), tokimonCards);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //Endpoints are specific URLs that the application exposes to handle various HTTP requests (like GET, POST, PUT, DELETE
    //example: For the TokimonCardController class, the following endpoints are defined:
    //GET /api/tokimon/all
    //These are basically points of interaction between the client and the server, allowing the client to
    //perform CRUD operations on the server's resources
}
