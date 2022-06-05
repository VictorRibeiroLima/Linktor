package codeanalysis.binding.scopes.identifier;

import codeanalysis.symbol.TypeSymbol;

import java.util.List;

public record FunctionIdentifier(String name, List<TypeSymbol> paramTypes) {
}
