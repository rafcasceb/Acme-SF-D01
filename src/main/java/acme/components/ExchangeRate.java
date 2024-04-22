
package acme.components;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeRate {

	// Attributes -------------------------------------------------------------

	public Map<String, String>				meta;	// example: {"last_updated_at": "2024-04-12T23:59:59Z"}

	public Map<String, Map<String, String>>	data;	// example: {"EUR": {"code": "EUR","value": 0.9390801563}}


	public Date getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = this.meta.get("last_updated_at").split("T")[0];
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

}
