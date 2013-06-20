import com.haulmont.yarg.formatters.CustomReport;
import com.haulmont.yarg.formatters.factory.DefaultFormatterFactory;
import com.haulmont.yarg.loaders.factory.DefaultLoaderFactory;
import com.haulmont.yarg.loaders.factory.PropertiesSqlLoaderFactory;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import com.haulmont.yarg.reporting.Reporting;
import com.haulmont.yarg.reporting.RunParams;
import com.haulmont.yarg.structure.Report;
import com.haulmont.yarg.structure.ReportOutputType;
import com.haulmont.yarg.structure.ReportTemplate;
import com.haulmont.yarg.structure.impl.BandBuilder;
import com.haulmont.yarg.structure.impl.BandData;
import com.haulmont.yarg.structure.impl.ReportBuilder;
import com.haulmont.yarg.structure.impl.ReportTemplateBuilder;
import com.haulmont.yarg.util.properties.DefaultPropertiesLoader;
import junit.framework.Assert;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author degtyarjov
 * @version $Id$
 */

public class CustomTemplateTest {
    @Test
    public void testReporting() throws Exception {
        TestDatabase testDatabase = new TestDatabase();
        testDatabase.setUpDatabase();
        Report report = createReport();

        Reporting reporting = new Reporting();
        reporting.setFormatterFactory(new DefaultFormatterFactory());
        reporting.setLoaderFactory(new DefaultLoaderFactory().setSqlDataLoader(new PropertiesSqlLoaderFactory(new DefaultPropertiesLoader()).create()));

        ReportOutputDocument reportOutputDocument = reporting.runReport(new RunParams(report), new FileOutputStream("./result/result.custom"));

        Assert.assertEquals("myFileName.txt", reportOutputDocument.getDocumentName());

        testDatabase.stop();
    }

    private Report createReport() throws IOException {
        ReportBuilder report = new ReportBuilder()
                .band(new BandBuilder()
                        .name("Band1")
                        .dataSet("", "select 'myFileName.txt' as file_name,login as col1, password as col2 from user", "sql")
                        .build()
                );
        report.template(
                new ReportTemplateBuilder()
                        .code(ReportTemplate.DEFAULT_TEMPLATE_CODE)
                        .documentName("result.none")
                        .outputType(ReportOutputType.custom)
                        .outputNamePattern("${Band1.FILE_NAME}")
                        .custom(new CustomReport() {
                            @Override
                            public byte[] createReport(Report report, BandData rootBand, Map<String, Object> params) {
                                return "Generated by custom report".getBytes();
                            }
                        })
                        .build())
                .name("report");

        return report.build();
    }
}
