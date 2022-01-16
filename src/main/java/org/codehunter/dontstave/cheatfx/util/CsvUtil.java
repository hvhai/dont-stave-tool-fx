package org.codehunter.dontstave.cheatfx.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.codehunter.dontstave.cheatfx.App;
import org.codehunter.dontstave.cheatfx.model.Item;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public final class CsvUtil {
    public static final String IMAGE_URL_PATTERN = "(http)?s?:?(//[^\"']*\\.(?:jpg|jpeg|gif|png|svg)).*";
    public static final String URL_PATTERN = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    public static List<Item> getListItemFromCsv(String path, boolean isUseStaticImage) {
        try (Reader reader = new FileReader(path, StandardCharsets.UTF_8)) {
            List<Item> itemList = new ArrayList<>();
            CSVReader csvReader = new CSVReader(reader);
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (isUseStaticImage) {
//                    String imageUrl = String.valueOf(Paths.get(CsvUtil.class.getClassLoader().getResource("image/" + line[1] + ".png").toURI()));
//                    String imageUrl = String.valueOf(Paths.get(ClassLoader.getSystemResource("/image/" + line[1] + ".png").toURI()));
//                    String imageUrl = String.valueOf(classloader.getResource("image/" +  line[1] + ".png"));
//                    String imageUrl =path.replace("inventory_data.csv","image/" +  line[1] + ".png");
                    String imageUrl = "image/" + line[1] + ".png";
                    File file = new File("src/main/resources/image/" + line[1] + ".png");
                    if (file.exists()) {
                        itemList.add(new Item(line[0], line[1], line[2], imageUrl));
                    }else {

                        itemList.add(new Item(line[0], line[1], line[2], "image/default.png"));
                    }
                } else {
                    Optional.of(line)
                            .filter(e -> Pattern.matches(URL_PATTERN, e[2]))
                            .map(e -> new Item(e[0], e[1], e[2]))
                            .filter(item -> Pattern.matches(IMAGE_URL_PATTERN, item.imageUrl()))
                            .ifPresent(itemList::add);
                }
            }
            return itemList;
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        List<Item> listItemFromCsv = CsvUtil.getListItemFromCsv(String.valueOf(Paths.get(ClassLoader.getSystemResource("inventory_data.csv").toURI())), false);

        for (Item item : listItemFromCsv) {
            System.out.println("create menu item" + item.name());
            try (InputStream in = new URL(item.imageUrl()).openStream()) {
                Files.copy(in, Paths.get("F:/temp/image/" + item.code() + ".png"));
            } catch (FileAlreadyExistsException existsException) {
                System.out.println("item exist" + item.code());
            }

        }
    }

}
