package io.github.cchzc.weather.setting;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CountyRepository {
    private static List<County> counties;
    public CountyRepository(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        Gson gson = new Gson();

        Type userListType = new TypeToken<ArrayList<County>>(){}.getType();

        counties = gson.fromJson(new InputStreamReader(
                assetManager.open("county_town.json")), userListType);
    }

    public List<County> getCounties() {
        return counties;
    }

    public CharSequence[] getCountyNames(){
        if(Locale.getDefault().equals(Locale.TAIWAN)) {
            return counties.stream().map(county->county.getName().getC())
                    .toArray(CharSequence[]::new);
        } else {
            return counties.stream().map(county->county.getName().getE())
                    .toArray(CharSequence[]::new);
        }
    }

    public CharSequence[] getCountyIds(){
        return counties.stream().map(county->""+county.getId())
                .toArray(CharSequence[]::new);
    }

    public CharSequence[] getTownNames(int countyId){
        Optional<County> county = counties.stream().filter(c->c.getId()==countyId)
                                .findFirst();
        if(Locale.getDefault().equals(Locale.TAIWAN)) {
            return county.map(value -> value.getTown().stream().map(t -> t.getName().getC())
                    .toArray(CharSequence[]::new)).orElse(new CharSequence[0]);
        } else {
            return county.map(value -> value.getTown().stream().map(t -> t.getName().getE())
                    .toArray(CharSequence[]::new)).orElse(new CharSequence[0]);
        }
    }
    public CharSequence[] getTownIds(int countyId){
        Optional<County> county = counties.stream().filter(c->c.getId()==countyId)
                .findFirst();
        return county.map(value -> value.getTown().stream().map(t -> "" + t.getId())
                .toArray(CharSequence[]::new)).orElse(new CharSequence[0]);
    }
}
