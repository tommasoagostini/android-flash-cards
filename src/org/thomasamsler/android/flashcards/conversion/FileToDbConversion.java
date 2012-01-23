package org.thomasamsler.android.flashcards.conversion;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.thomasamsler.android.flashcards.AppConstants;
import org.thomasamsler.android.flashcards.db.DataSource;
import org.thomasamsler.android.flashcards.model.Card;
import org.thomasamsler.android.flashcards.model.CardSet;

import android.content.Context;
import android.util.Log;

public class FileToDbConversion {

	public FileToDbConversion() { }
	
	public void convert(Context context, DataSource dataSource) {
		
		// Get all the file names
		String[] fileNames = context.fileList();
		
		for(String fileName : fileNames) {
			
			CardSet cardSet = dataSource.createCardSet(fileName);
			int displayOrder = 1;
			
			FileInputStream fis;
			BufferedReader reader = null;
			
			try {

				fis =  context.openFileInput(fileName);
				reader = new BufferedReader(new InputStreamReader(fis));
				String card;

				while((card = reader.readLine()) != null) {

					if(null != card && !"".equals(card) && 3 <= card.length()) {

						String[] words = card.split(":");
						
						if(words.length == 2) {
							
							Card newCard = new Card();
							newCard.setQuestion(words[0]);
							newCard.setAnswer(words[1]);
							newCard.setCardSetId(cardSet.getId());
							newCard.setDisplayOrder(displayOrder);
							displayOrder += 1;
							dataSource.createCard(newCard);
						}
					}
				}
			}
			catch(FileNotFoundException e) {

				Log.w(AppConstants.LOG_TAG, "FileNotFoundException: while reading words from file", e);
			}
			catch(IOException e) {

				Log.w(AppConstants.LOG_TAG, "IOException: while reading words from file", e);
			}
			finally {
				
				try {
					
					if(null != reader) {
					
						reader.close();
					}
				}
				catch (IOException e) {
					
					Log.e(AppConstants.LOG_TAG, "IOException", e);
				}
			}
			
			cardSet.setCardCount(displayOrder - 1);
			dataSource.updateCardSet(cardSet);
			
			context.deleteFile(fileName);
		}
	}
}
