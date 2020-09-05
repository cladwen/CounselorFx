/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package control.services;

import business.facade.LocalFacade;
import business.services.ComparatorFactory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.Artefato;
import model.Exercito;
import model.Habilidade;
import model.Local;
import model.Personagem;
import msgs.BaseMsgs;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import persistenceCommons.BundleManager;
import persistenceCommons.SettingsManager;
import utils.StringRet;

/**
 *
 * @author jmoura
 */
public class LocalConverter implements Serializable {

    private static final Log log = LogFactory.getLog(LocalConverter.class);
    private static final LocalFacade localFacade = new LocalFacade();
//    private static final ListFactory listFactory = new ListFactory();
    private static final BundleManager labels = SettingsManager.getInstance().getBundleManager();

    public static String getInfo(Local local) {
        StringRet ret = new StringRet();
        //cidade
        ret.add(CidadeConverter.getInfo(local.getCidade()));
        //local
        //"Local : @ 3103 em Planície O Clima é Polar"
        ret.add(String.format(labels.getString("TERRENO.CLIMA"),
                local.getTerreno().getNome(),
                BaseMsgs.localClima[local.getClima()]));
        //old landmark print
//        if (localFacade.isTerrainLandmark(local)) {
//            ret.add(labels.getString("LANDMARK.LOCAL") + ";");
//            for (Habilidade feature : localFacade.getTerrainLandmark(local)) {
//                ret.addTab(ConverterFactory.getLandmarkName(feature.getCodigo()));
//            }
//        }
        //landmarks and others
        ret.add("\n");
        for (Habilidade hab : local.getHabilidades().values()) {
            if (hab.getCodigo().equals(";-;")) {
                continue;
            }
            //ret.add(labels.getString("LANDMARK.LOCAL") + " " + ConverterFactory.getLandmarkName(hab.getCodigo()));
            ret.add(hab.getNome());
        }
        //City 
        if (localFacade.isCidade(local)) {
            for (Habilidade hab : local.getCidade().getHabilidades().values()) {
                if (hab.getCodigo().equals(";-;")) {
                    continue;
                }
                ret.add(hab.getNome());
            }
        }
        //personagens
        if (local.getPersonagens().values().size() > 0) {
            ret.add("\n");
            ret.add(labels.getString("PERSONAGENS.LOCAL"));
            if (SettingsManager.getInstance().getConfig("HexInfoPcSorting", "N").equals("N")) {
                //sort by nation
                final List<Personagem> personagens = new ArrayList<>(local.getPersonagens().values());
                ComparatorFactory.getComparatorNationSorter(personagens);

                for (Personagem personagem : personagens) {
                    ret.add(PersonagemConverter.getInfo(personagem));
                }
            } else {
                //sort alphabetcaly
                for (Personagem personagem : local.getPersonagens().values()) {
                    ret.add(PersonagemConverter.getInfo(personagem));
                }
            }
        }
        //exercitos
        if (local.getExercitos().values().size() > 0) {
            ret.add("\n");
            ret.add(labels.getString("EXERCITOS"));
            for (Exercito exercito : local.getExercitos().values()) {
                ret.add(ExercitoConverter.getInfo(exercito));
            }
        }
        //artefatos
        if (local.getArtefatos().values().size() > 0) {
            ret.add("\n");
            ret.add(labels.getString("ARTEFATOS"));
            for (Artefato artefato : local.getArtefatos().values()) {
                ret.add(ArtefatoConverter.getInfo(artefato));
            }
        }
        return ret.getText();
    }
    
    public static Map<String, String> getInfoMap(Local local) {
        
        Map<String, String> infoMap = new HashMap<>();
        
        StringBuilder infoCity = new StringBuilder();
                
     //   infoCity.append(String.format(labels.getString("TERRENO.CLIMA"), local.getTerreno().getNome(), BaseMsgs.localClima[local.getClima()]));
        
        //landmarks and others
        infoCity.append("\n");
        for (Habilidade hab : local.getHabilidades().values()) {
            if (hab.getCodigo().equals(";-;")) {
                continue;
            }
            //ret.add(labels.getString("LANDMARK.LOCAL") + " " + ConverterFactory.getLandmarkName(hab.getCodigo()));
            infoCity.append(hab.getNome());
        }
       
        infoCity.append(CidadeConverter.getInfo(local.getCidade(), false).stream().map(Object::toString).collect(Collectors.joining("\n")));
        infoCity.trimToSize();
        if (infoCity.length() > 0) {
            infoMap.put(labels.getString("CIDADES"), infoCity.toString());
            
        }
        if (local.getCidade() != null) {
            String listProd = CidadeConverter.getInfoProduction(local.getCidade()).stream().map(Object::toString).map(String::trim).collect(Collectors.joining("|"));
            infoMap.put("PRODUCTION", listProd);
        } else {
            infoMap.remove("PRODUCTION");
        }
        
        
        StringBuilder pjInfo = new StringBuilder();
        
        if (local.getPersonagens().values().size() > 0) {
     
            if (SettingsManager.getInstance().getConfig("HexInfoPcSorting", "N").equals("N")) {
                //sort by nation
                final List<Personagem> personagens = new ArrayList<>(local.getPersonagens().values());
                ComparatorFactory.getComparatorNationSorter(personagens);

                for (Personagem personagem : personagens) {
                  pjInfo.append(PersonagemConverter.getInfo(personagem).stream().map(Object::toString).collect(Collectors.joining("\n")));                  
                  pjInfo.append("\n");
                }
            } else {
                //sort alphabetcaly
                for (Personagem personagem : local.getPersonagens().values()) {
                    pjInfo.append(PersonagemConverter.getInfo(personagem).stream().map(Object::toString).collect(Collectors.joining("\n")));                  
                    pjInfo.append("\n");
                }
            }
        }
        if (pjInfo.length() > 0) {
            infoMap.put(labels.getString("PERSONAGENS.LOCAL"), pjInfo.toString());
        } else {
            infoMap.remove(labels.getString("PERSONAGENS.LOCAL"));
        }
        
        StringBuilder armyInfo = new StringBuilder();
         if (local.getExercitos().values().size() > 0) {
          
            for (Exercito exercito : local.getExercitos().values()) {
                armyInfo.append(ExercitoConverter.getInfo(exercito).stream().map(Object::toString).collect(Collectors.joining("\n")));                  
                armyInfo.append("\n");
            }
        }
        
        infoMap.put("EXERCITOS", armyInfo.toString());
        
        //artefatos
        StringBuilder artefactInfo = new StringBuilder();
        if (local.getArtefatos().values().size() > 0) {           
            
            for (Artefato artefato : local.getArtefatos().values()) {
                artefactInfo.append(ArtefatoConverter.getInfo(artefato));
                artefactInfo.append("\n");
            }
        }
        infoMap.put("ARTEFATOS", artefactInfo.toString());
        return infoMap;
    }
}
