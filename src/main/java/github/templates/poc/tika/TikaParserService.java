package github.templates.poc.tika;

import github.templates.poc.model.TikaTO;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

@Service
public class TikaParserService {

    private final Parser parser;

    @Autowired
    public TikaParserService(Parser parser) {
        this.parser = parser;
    }

    public TikaTO parseFile(InputStream fileInputStream) throws TikaException, SAXException, IOException {
        Metadata metadata = new Metadata();
        BodyContentHandler handler = new BodyContentHandler();
        ParseContext context = new ParseContext();
        context.set(Parser.class, parser);
        try {
            parser.parse(fileInputStream, handler, metadata, context);
            return new TikaTO(handler.toString());
        } finally {
            fileInputStream.close();
        }
    }

}
