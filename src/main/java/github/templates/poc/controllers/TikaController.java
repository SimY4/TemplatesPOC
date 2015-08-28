package github.templates.poc.controllers;

import github.templates.poc.model.TikaTO;
import github.templates.poc.tika.TikaParserService;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;

@RestController
@RequestMapping("/tika")
public class TikaController {

    private final TikaParserService tikaParserService;

    @Autowired
    public TikaController(TikaParserService tikaParserService) {
        this.tikaParserService = tikaParserService;
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TikaTO uploadTemplate(@RequestParam("file") MultipartFile file) throws IOException, TikaException, SAXException {
        return tikaParserService.parseFile(file.getInputStream());
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleFileUploadFailure() { }

}
