package io;

import codeanalysis.symbol.FunctionSymbol;
import codeanalysis.symbol.ParameterSymbol;
import codeanalysis.symbol.Symbol;
import codeanalysis.symbol.TypeSymbol;
import util.ConsoleColors;

import java.io.PrintWriter;

public class SymbolWriter {
    private final PrintWriter out;

    private SymbolWriter(PrintWriter out) {
        this.out = out;
    }

    public static void writeTo(PrintWriter out, Symbol symbol) {
        var writer = new SymbolWriter(out);
        writer.writeTo(symbol);
    }

    private void writeTo(Symbol symbol) {
        switch (symbol.getKind()) {
            case FUNCTION -> writeFunction((FunctionSymbol) symbol);
            case PARAMETER -> writeParam((ParameterSymbol) symbol);
            case TYPE -> writeType((TypeSymbol) symbol);
        }
    }

    private void writeType(TypeSymbol type) {
        writePunctuation(":");
        writeKeyword(type.getName());
    }

    private void writeParam(ParameterSymbol param) {
        writeParamName(param.getName());
        writeTo(param.getType());
    }

    private void writeFunction(FunctionSymbol symbol) {
        writeKeyword("function");
        writeFunctionName(symbol.getName());
        writePunctuation("(");
        if (!symbol.getParameters().isEmpty()) {
            var lastParam = symbol.getParameters().get(symbol.getParameters().size() - 1);
            for (var param : symbol.getParameters()) {
                writeTo(param);
                if (!param.equals(lastParam)) {
                    writePunctuation(",");
                }
            }
        }
        writePunctuation(")");
        writeTo(symbol.getType());

    }

    private void write(String text) {
        out.print(text + " ");
    }

    private void writePunctuation(String text) {
        out.print(ConsoleColors.WHITE_BOLD);
        write(text);
        out.print(ConsoleColors.RESET);
    }

    private void writeIdentifier(String text) {
        out.print(ConsoleColors.PURPLE_255);
        write(text);
        out.print(ConsoleColors.RESET);
    }


    private void writeKeyword(String text) {
        out.print(ConsoleColors.PINK_255);
        write(text);
        out.print(ConsoleColors.RESET);
    }

    private void writeFunctionName(String text) {
        out.print(ConsoleColors.GREEN_255);
        write(text);
        out.print(ConsoleColors.RESET);
    }

    private void writeParamName(String text) {
        out.print(ConsoleColors.ORANGE_255);
        write(text);
        out.print(ConsoleColors.RESET);
    }
}
