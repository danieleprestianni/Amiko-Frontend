package io.amiko.app.dataanalysis;

import io.amiko.app.doctorsapp.demo.RecordModel;

public interface IRecordModelListener {

	/**
	 * 
	 * @param rec
	 * @return NULL is the new record model has not generateD a new Pattern
	 *         Recognized Event, otherwise it returns a new RecordModel.
	 * 
	 */
	public RecordModel onNewRecordModel(RecordModel rec);

}
