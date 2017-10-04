package de.adorsys.sts.worksheetloader;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@Component
public class DataSheetLoader {

	@Autowired
	private LoginLoader loginLoader;


	public InputStream loadFile() {

		String dataSheetFile = "sts_login_data.xls";
		InputStream stream = DataSheetLoader.class.getResourceAsStream("/" + dataSheetFile);
		return stream;
	}

	public void loadDataSheet(InputStream dataStream) {

		try {
			HSSFWorkbook workbook = new HSSFWorkbook(dataStream);
			updateLogin(workbook);

			IOUtils.closeQuietly(dataStream);

		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			IOUtils.closeQuietly(dataStream);
		}

	}

	private void updateLogin(HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.getSheet("Login");
		if (sheet == null)
			return;

		Iterator<Row> rowIterator = sheet.rowIterator();
		rowIterator.next();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			loginLoader.update(row);
		}
	}
}
