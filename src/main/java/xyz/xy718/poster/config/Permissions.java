package xyz.xy718.poster.config;

import lombok.Getter;

public enum Permissions {
	DEBUG("xydp.debug"),
	PLUGIN_INFO("xydp.plugin.info"),
	RELOAD("xydp.plugin.reload"),
	
	;
	@Getter private String permission;
	Permissions(String permission) {
		this.permission=permission;
	}
}
