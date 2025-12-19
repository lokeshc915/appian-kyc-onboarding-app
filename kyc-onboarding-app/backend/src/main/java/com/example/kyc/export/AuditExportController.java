
package com.example.kyc.export;

import com.example.kyc.audit.AuditEvent;
import com.example.kyc.audit.AuditEventRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.io.OutputFile;
import org.apache.parquet.io.PositionOutputStream;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-export")
public class AuditExportController {

  private final AuditEventRepository repo;

  public AuditExportController(AuditEventRepository repo) {
    this.repo = repo;
  }

  @GetMapping("/csv")
  @PreAuthorize("hasRole('ADMIN')")
  public void exportCsv(HttpServletResponse res) throws IOException {
    List<AuditEvent> events = repo.findAll();

    res.setStatus(200);
    res.setContentType("text/csv");
    res.setHeader("Content-Disposition", "attachment; filename=audit_export.csv");

    try (Writer w = new OutputStreamWriter(res.getOutputStream(), StandardCharsets.UTF_8)) {
      w.write("eventAt,caseId,action,actor,fromStatus,toStatus,message\n");
      for (AuditEvent e : events) {
        w.write(safe(e.getEventAt()) + "," +
            safeId(e.getOnboardingCase() != null ? e.getOnboardingCase().getId() : null) + "," +
            safeStr(e.getAction()) + "," +
            safeStr(e.getActor()) + "," +
            safeStr(e.getFromStatus()) + "," +
            safeStr(e.getToStatus()) + "," +
            safeCsv(e.getMessage()) + "\n");
      }
    }
  }

  @GetMapping("/parquet")
  @PreAuthorize("hasRole('ADMIN')")
  public void exportParquet(HttpServletResponse res) throws Exception {
    List<AuditEvent> events = repo.findAll();

    res.setStatus(200);
    res.setContentType("application/octet-stream");
    res.setHeader("Content-Disposition", "attachment; filename=audit_export.parquet");

    Schema schema = SchemaBuilder.record("AuditEvent")
        .namespace("com.example.kyc.export")
        .fields()
        .requiredString("eventAt")
        .optionalLong("caseId")
        .requiredString("action")
        .optionalString("actor")
        .optionalString("fromStatus")
        .optionalString("toStatus")
        .optionalString("message")
        .endRecord();

    OutputFile out = new OutputFile() {
      @Override public PositionOutputStream create(long blockSizeHint) { return new ServletPOS(res); }
      @Override public PositionOutputStream createOrOverwrite(long blockSizeHint) { return new ServletPOS(res); }
      @Override public boolean supportsBlockSize() { return false; }
      @Override public long defaultBlockSize() { return 0; }
    };

    try (ParquetWriter<GenericRecord> writer = AvroParquetWriter.<GenericRecord>builder(out)
        .withSchema(schema)
        .withCompressionCodec(CompressionCodecName.SNAPPY)
        .build()) {
      for (AuditEvent e : events) {
        GenericRecord r = new GenericData.Record(schema);
        r.put("eventAt", safe(e.getEventAt()));
        r.put("caseId", e.getOnboardingCase() != null ? e.getOnboardingCase().getId() : null);
        r.put("action", String.valueOf(e.getAction()));
        r.put("actor", e.getActor());
        r.put("fromStatus", e.getFromStatus() != null ? String.valueOf(e.getFromStatus()) : null);
        r.put("toStatus", e.getToStatus() != null ? String.valueOf(e.getToStatus()) : null);
        r.put("message", e.getMessage());
        writer.write(r);
      }
    }
  }

  private static String safe(OffsetDateTime t) { return t == null ? "" : t.toString(); }
  private static String safeId(Long id) { return id == null ? "" : id.toString(); }
  private static String safeStr(Object o) { return o == null ? "" : String.valueOf(o); }
  private static String safeCsv(String s) {
    if (s == null) return "";
    String v = s.replace(""", """");
    return """ + v + """;
  }

  static class ServletPOS extends PositionOutputStream {
    private final OutputStream os;
    private long pos = 0L;
    ServletPOS(HttpServletResponse res) {
      try { this.os = res.getOutputStream(); }
      catch (IOException e) { throw new RuntimeException(e); }
    }
    @Override public long getPos() { return pos; }
    @Override public void write(int b) throws IOException { os.write(b); pos++; }
    @Override public void write(byte[] b, int off, int len) throws IOException { os.write(b, off, len); pos += len; }
    @Override public void flush() throws IOException { os.flush(); }
    @Override public void close() throws IOException { os.close(); }
  }
}
