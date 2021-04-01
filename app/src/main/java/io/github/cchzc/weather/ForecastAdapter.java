package io.github.cchzc.weather;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater li;
    private final List<ForecastData> data;

    public ForecastAdapter(Context context, List<ForecastData> data){
        this.context = context;
        this.data = data;
        this.li = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) { return position; }

    private static class ViewHolder{
        TextView tv_dateTime;
        ImageView iv_weather;
        ImageView iv_comfort;
        ImageView iv_uvi_ic;
        TextView tv_uvi;
        TextView tv_temp;
        TextView tv_a_temp;
        TextView tv_rain;
        TextView tv_rh;
        ImageView iv_wind_ic;
        ImageView iv_wind_d_ic;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            view = li.inflate(R.layout.list_item, viewGroup, false);
            holder = new ViewHolder();
            holder.tv_dateTime = view.findViewById(R.id.tv_dateTime);
            holder.iv_weather = view.findViewById(R.id.iv_weather);
            holder.iv_comfort = view.findViewById(R.id.iv_comfort);
            holder.iv_uvi_ic = view.findViewById(R.id.iv_uvi_ic);
            holder.tv_uvi = view.findViewById(R.id.tv_uvi);
            holder.tv_temp = view.findViewById(R.id.tv_temp);
            holder.tv_a_temp = view.findViewById(R.id.tv_a_temp);
            holder.tv_rain = view.findViewById(R.id.tv_rain);
            holder.tv_rh = view.findViewById(R.id.tv_rh);
            holder.iv_wind_ic = view.findViewById(R.id.iv_wind_ic);
            holder.iv_wind_d_ic = view.findViewById(R.id.iv_wind_d_ic);

            view.setTag(holder);
        }else {
            holder = (ViewHolder)view.getTag();
        }

        ForecastData data = this.data.get(i);

        int id = context.getResources().getIdentifier(data.getWeatherIcon() , "drawable", context.getPackageName());
        holder.iv_weather.setImageResource(id);

        Resources r = context.getResources();

        if(data.getDataType().equals(ForecastData.Type.HOUR)) {
            String dateTimePatten = "HH:00";
            if(data.getDateTime().getHour() == 0)
                dateTimePatten = "HH:00\nMM/dd\n(E)";
            holder.tv_dateTime.setText(data.getDateTime().format(DateTimeFormatter.ofPattern(dateTimePatten, Locale.getDefault())));
            int comfortId = context.getResources().getIdentifier("comfort_"+(data.getComfortIndex().ordinal()-1) , "drawable", context.getPackageName());
            holder.iv_comfort.setImageResource(comfortId);
            holder.iv_uvi_ic.setVisibility(View.INVISIBLE);
            holder.tv_uvi.setVisibility(View.INVISIBLE);
            holder.tv_temp.setText(""+data.getMinTemp());
            holder.tv_a_temp.setText(""+data.getMinATemp());
        } else {
            String datetime = data.getDateTime().format(DateTimeFormatter.ofPattern(data.getDateTime().getHour() > 12? "a":"a\nMM/dd\n(E)"));
            datetime = datetime.replace(r.getString(R.string.am), r.getString(R.string.day))
                                .replace(r.getString(R.string.pm) , r.getString(R.string.night));
            holder.tv_dateTime.setText(datetime);
            holder.iv_comfort.setVisibility(View.INVISIBLE);
            holder.tv_uvi.setText(""+data.getUVIndex());
            if(data.getUVIndex() < 0){
                holder.iv_uvi_ic.setVisibility(View.INVISIBLE);
                holder.tv_uvi.setVisibility(View.INVISIBLE);
            } else {
                holder.iv_uvi_ic.setVisibility(View.VISIBLE);
                holder.tv_uvi.setVisibility(View.VISIBLE);
            }
            holder.tv_temp.setText(String.format(Locale.getDefault(), "%d~%d", data.getMinTemp(), data.getMaxTemp()));
            holder.tv_a_temp.setText(String.format(Locale.getDefault(),"%d~%d", data.getMinATemp(), data.getMaxATemp()));
        }
        holder.tv_rain.setText((data.getRain()> -1) ? data.getRain()+"%" : "-");
        holder.tv_rh.setText(data.getHumidity()+"%");
        int windId = context.getResources().getIdentifier("wi_wind_beaufort_"+ data.getWind() ,
                "drawable", context.getPackageName());
        holder.iv_wind_ic.setImageResource(windId);
        holder.iv_wind_d_ic.setRotation(data.getWindDirection().ordinal()*45+180);
        return view;
    }
}
