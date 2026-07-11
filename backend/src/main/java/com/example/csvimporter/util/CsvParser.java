package com.example.csvimporter.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Component
public class CsvParser {

    public List<List<String>> parseCsvAsLists(InputStream inputStream) throws IOException {

        CSVParser parser = CSVFormat.DEFAULT.parse(new InputStreamReader(inputStream));

        List<List<String>> rows = new ArrayList<>();

        for (CSVRecord record : parser) {

            List<String> row = new ArrayList<>();

            record.forEach(row::add);

            rows.add(row);
        }

        return rows;
    }

    public List<Map<String,String>> parseCsvAsMaps(InputStream inputStream) throws IOException {

        List<List<String>> rows = parseCsvAsLists(inputStream);

        List<Map<String,String>> result = new ArrayList<>();

        if(rows.isEmpty())
            return result;

        List<String> headers = rows.get(0);

        for(int i=1;i<rows.size();i++){

            List<String> row = rows.get(i);

            Map<String,String> map = new LinkedHashMap<>();

            for(int j=0;j<headers.size();j++){

                String value="";

                if(j<row.size())
                    value=row.get(j);

                map.put(headers.get(j),value);
            }

            result.add(map);
        }

        return result;
    }

}