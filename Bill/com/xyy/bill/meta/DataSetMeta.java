package com.xyy.bill.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.bill.meta.DataTableMeta.TableType;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;

/**
 * <DataSet DataSource="" Version="1.0.0" />
 * 
 * @author evan
 *
 */
@XmlComponent(tag = "DataSet", type = DataSetMeta.class)
public class DataSetMeta {
	private String dataSource;
	private String version;

	private List<DataTableMeta> dataTableMetas = new ArrayList<DataTableMeta>();

	public DataSetMeta() {
		super();
	}

	@XmlAttribute(name = "dataSource", tag = "DataSource", type = String.class)
	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	@XmlAttribute(name = "version", tag = "Version", type = String.class)
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void addDataTableMeta(DataTableMeta table) {
		this.dataTableMetas.add(table);
	}

	public void removeDataTableMeta(DataTableMeta table) {
		this.dataTableMetas.remove(table);
	}

	@XmlList(name = "dataTableMetas", subTag = "DataTable", type = DataTableMeta.class)
	public List<DataTableMeta> getDataTableMetas() {
		return dataTableMetas;
	}

	public void setDataTableMetas(List<DataTableMeta> dataTableMetas) {
		this.dataTableMetas = dataTableMetas;
	}

	public List<DataTableMeta> getEntityBodyDataTables() {
		List<DataTableMeta> result=new ArrayList<>();
		for(DataTableMeta table:this.dataTableMetas){
			if(!table.getHead() && table.getTableType()==TableType.Table){
				result.add(table);
			}
		}
		return result;
	}
	
	public DataTableMeta getHeadDataTable() {
		for(DataTableMeta table:this.dataTableMetas){
			if(table.getHead() && table.getTableType()==TableType.Table){
				return table;
			}
		}
		return null;
	}

	public DataTableMeta getTable(String table) {
		for(DataTableMeta dtm:this.dataTableMetas){
			if(dtm.getKey().equals(table)){
				return dtm;
			}
		}
		
		return null;
	}

	

	

}
