package br.com.zenon;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TransactionIngestor {
	
	public List<Transaction> read(String fileName) {
		
		Path path = Path.of(fileName);
	
		try {
			List<String> lines = Files.readAllLines(path);
			return lines.stream().skip(1).limit(1000).map(this::parseTransaction).toList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Erro ao ler o arquivo: " + fileName, e);
		}
	}
	
	private Transaction parseTransaction(String line){
		String[] chunks = line.split(",");
		
		int step = Integer.parseInt(chunks[0]);
		TransactionType type = TransactionType.valueOf(chunks[1]);
		
		BigDecimal amount = new BigDecimal(chunks[2]); 
		
		var origin = new TransactionCustomer(chunks[3], new BigDecimal(chunks[4]), new BigDecimal(chunks[5]));
		var recipient = new TransactionCustomer(chunks[6], new BigDecimal(chunks[7]), new BigDecimal(chunks[8]));
		
		boolean isFraud = "1".equals(chunks[9]);
		
		boolean isFlaggedFraud = "1".equals(chunks[10]);
		
		return new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud);
	}

}
