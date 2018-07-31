package com.atum.tunetube.sql;

import com.atum.tunetube.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SQLiteDatabaseTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setupDB() throws IOException {
        File tempFile = testFolder.newFile("testdb1");
        InputStream upgradeDB = getInstrumentation().getContext().getResources().openRawResource(R.raw.databaseupdates);
        DatabaseConnection db = new DatabaseConnection(upgradeDB, tempFile);
    }

}
