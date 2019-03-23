package com.securitybox.editokenizerRest;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import com.securitybox.constants.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static com.securitybox.editokenizerRest.TokenizerApplication.edifact;

@RestController
@EnableWebMvc
@RequestMapping("/tokenizer")
@Api(value="Tokenization REST API.", description="API operation supported for tokenization system")
public class TokenizerContoller {

    private static final String template = "%s!";
    private final AtomicLong counter = new AtomicLong();

    private String EdifactTokenizer(String input, JSONArray elementsToDeTokenize, ArrayList senderIdList, ArrayList receiverIdList,String operation){
        String response="";
        try {
            if(operation.equalsIgnoreCase(Constants.TOKENIZER_METHOD_TOKENIZE))
                response = edifact.docuemntHandler(Constants.TOKENIZER_METHOD_TOKENIZE,elementsToDeTokenize,input,senderIdList,receiverIdList);
            else if(operation.equalsIgnoreCase(Constants.TOKENIZER_METHOD_DETOKENIZE))
                response = edifact.docuemntHandler(Constants.TOKENIZER_METHOD_DETOKENIZE,elementsToDeTokenize,input,senderIdList,receiverIdList);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return response;
    }

    @PostMapping
    @RequestMapping(value = "/tokenize", method = RequestMethod.POST)
    @ApiOperation(value = "Tokenize an electronic message")
    //request message must contain the message type, segments to be tokenized as a parameters
    //http://localhost:8080/tokenizer/edidoc?msgType=EDIFACT
    public TokenizerDocument tokenize(@RequestParam(value="ElementsToTokenize",required = true) JSONArray elementsToTokenize,
                                      @RequestParam(value="SenderIds",required = false) ArrayList senderIdList,
                                      @RequestParam(value="ReceiverIds",required = false) ArrayList receiverIdList,
                                      @RequestParam(value="MessageType",required = false) String messageType,
                                      @RequestBody() String document ) {


        //log for debugging...
        System.out.println("Receieved Message : " + document);
        System.out.println("Receieved elemnts to be tokenized : " + elementsToTokenize);
        System.out.println("Receieved sender id list : " + senderIdList);
        System.out.println("Receieved receiver id list : " + receiverIdList);

        //call EDIFACT rokenizer service..
        return new TokenizerDocument(counter.incrementAndGet(),
                String.format(template, EdifactTokenizer(document,elementsToTokenize,senderIdList,receiverIdList,Constants.TOKENIZER_METHOD_TOKENIZE)));
    }


    @PostMapping
    @RequestMapping(value = "/de-tokenize",method = RequestMethod.POST)
    @ApiOperation(value = "De-Tokenize an electronic message")
    //request message must contain the message type, segments to be tokenized as a parameters
    //http://localhost:8080/tokenizer/edidoc?msgType=EDIFACT
    public TokenizerDocument detokenize(
            @RequestParam(value="ElementsToDeTokenize",required = true) JSONArray elementsToDeTokenize,
            @RequestParam(value="SenderIds",required = false) ArrayList senderIdList,
            @RequestParam(value="ReceiverIds",required = false) ArrayList receiverIdList,
            @RequestParam(value="MessageType",required = false) String messageType,
            @RequestBody(required = true) String document) {

        //log for debugging...
        System.out.println("Receieved Message : " + document);
        System.out.println("Receieved elemnts to be de-tokenized : " + elementsToDeTokenize);
        System.out.println("Receieved sender id list : " + senderIdList);
        System.out.println("Receieved receiver id list : " + receiverIdList);

        //call EDIFACT rokenizer service..
        return new TokenizerDocument(counter.incrementAndGet(),
                String.format(template, EdifactTokenizer(document,elementsToDeTokenize,senderIdList,receiverIdList,Constants.TOKENIZER_METHOD_DETOKENIZE)));
    }

    //Get a stored value of a token stored
    @ApiOperation(value = "De-Tokenize a given token.")
    @RequestMapping(value = "/de-tokenize", method = RequestMethod.GET)
    @ResponseBody
    public String getTokenValue(
            @RequestParam("token") String token) {
        String response = edifact.tokenizer.deTokenize(Integer.parseInt(token));
        if(response.equalsIgnoreCase(token))
            return "Token Not Found";
        else
            return response;
    }

    //Get access logs of a given token
    @ApiOperation(value = "Request audit logs of a token.")
    @RequestMapping(value = "/audit/logs", method = RequestMethod.GET)
    @ResponseBody
    public String getBarBySimplePathWithRequestParam(
            @RequestParam("token") String token) {
        return "This API method is not yet implemented for : " + token;
    }


}