package rexagon.rexaeco.utils;

import rexagon.rexaeco.RexaEco;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {

    // Bakiyeler için küsuratsız tam sayı formatı (Örn: 1.000.000)
    public static String format(RexaEco plugin, double amount) {
        NumberFormat format = NumberFormat.getInstance(new Locale("tr", "TR"));
        format.setGroupingUsed(true);
        return format.format((long) amount);
    }

    // Sadece market fiyatları için küsuratlı format (Örn: 0,2 veya 10,50)
    public static String formatPrice(double amount) {
        NumberFormat format = NumberFormat.getInstance(new Locale("tr", "TR"));
        format.setGroupingUsed(true);
        format.setMaximumFractionDigits(2); // En fazla 2 küsurat göster
        format.setMinimumFractionDigits(0); // Tam sayıysa küsuratı gizle (.0 yazmasın)
        return format.format(amount);
    }
}